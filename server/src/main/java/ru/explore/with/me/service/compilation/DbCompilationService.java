package ru.explore.with.me.service.compilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.explore.with.me.dto.compilation.CompilationDto;
import ru.explore.with.me.dto.compilation.NewCompilationDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.mapper.compilation.CompilationMapper;
import ru.explore.with.me.model.compilation.Compilation;
import ru.explore.with.me.model.compilation.CompilationEventsId;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.repository.compilation.CompilationEventsRepository;
import ru.explore.with.me.repository.compilation.CompilationRepository;
import ru.explore.with.me.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbCompilationService implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final CompilationEventsRepository compilationEventsRepository;

    @Autowired
    public DbCompilationService(CompilationRepository compilationRepository,
                                CompilationMapper compilationMapper,
                                EventRepository eventRepository,
                                CompilationEventsRepository compilationEventsRepository) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.compilationEventsRepository = compilationEventsRepository;
    }

    /**
     * Создание подборки событий с сохранением в БД.
     *
     * @param compilationDto Dto объект подборки
     * @return CompilationDto
     */
    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    /**
     * Удаление подборки. Также удаляет записи из связующей таблицы.
     * В случае отсутсвия подборки выбрасывается NotFoundException.
     *
     * @param compId Id подборки
     * @return String or NotFoundException
     */
    @Override
    public String deleteCompilation(long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            compilationEventsRepository.deleteAllByCompilationId(compId);
            return "Подборка удалена";
        }
        throw new NotFoundException("Подборка не найдена");
    }

    /**
     * Добавление события к подборке. При отсутвии события или подборки в БД выбрасывается исключение.
     *
     * @param compId  Id Подборки
     * @param eventId Id События
     * @return String or NotFoundException
     */
    @Override
    public String addEventToCompilation(long compId, long eventId) {
        Compilation compilation = getCompilationFromDb(compId);
        Event event = getEventFromDb(eventId);

        List<Event> events = compilation.getEvents();
        if (events.contains(event)) {
            return "Подборка уже содержит данное событие";
        }

        events.add(event);
        compilationRepository.save(compilation);
        return "Событие добавлено";
    }

    /**
     * Удаление события из подборки. Также удаляет значение из связующей таблицы.
     *
     * @param compId  Id Подборки
     * @param eventId Id События
     * @return String or NotFoundException
     */
    @Override
    public String removeEventFromCompilation(long compId, long eventId) {
        Compilation compilation = getCompilationFromDb(compId);

        compilation.getEvents().stream()
                .filter(event -> event.getId() != eventId)
                .collect(Collectors.toList());

        compilationRepository.save(compilation);
        compilationEventsRepository.deleteById(new CompilationEventsId(eventId, compId));
        return "Событие удалено из подборки";
    }

    /**
     * Закрепление/открепление подборки на главной странице в зависимости от передаваемого параметра.
     *
     * @param pin    статус для измемения. true - закрепить, false - откепить
     * @param compId Id подборки
     * @return String or NotFoundException
     */
    @Override
    public String pinCompilation(long compId, boolean pin) {
        Compilation compilation = getCompilationFromDb(compId);

        if (pin) {
            if (compilation.isPinned()) {
                return "Подборка уже закреплена";
            }
        } else if (!compilation.isPinned()) {
            return "Подборка не была закреплена на главной";
        }

        compilation.setPinned(pin);
        compilationRepository.save(compilation);

        if (pin) {
            return "Подборка закреплена";
        }
        return "Подборка откреплена";
    }

    /**
     * Получение подборок событий в зависимости от параметра pinned с пагинацией.
     *
     * @param pinned параметр выборки.
     *               pinned == null - Все подборки;
     *               pinned == true - Закрепленные подборки;
     *               pinned == false - Не закрепленные подборки.
     * @param from   Сколько элементов необходимо пропустить
     * @param size   Сколько элементов отображаются на странице
     * @return List<CompilationDto> Список подборок
     */
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findAllByPinnedIs(pinned, page);
        }

        return compilations.stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение подборки по ее id. В случае отсутсвия подборки с заданым id
     * выбрасывается исключение.
     *
     * @param compId Id подборки
     * @return CompilationDto or NotFoundException
     */
    @Override
    public CompilationDto getCompilation(long compId) {
        return compilationMapper.toCompilationDto(
                getCompilationFromDb(compId));
    }

    private Compilation getCompilationFromDb(long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена"));
    }

    private Event getEventFromDb(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("События не существует"));
    }
}

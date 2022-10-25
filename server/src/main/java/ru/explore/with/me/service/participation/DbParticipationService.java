package ru.explore.with.me.service.participation;

import org.springframework.stereotype.Service;
import ru.explore.with.me.dto.participation.ParticipationRequestDto;
import ru.explore.with.me.exeption.NotFoundException;
import ru.explore.with.me.exeption.ValidationException;
import ru.explore.with.me.mapper.participation.ParticipationMapper;
import ru.explore.with.me.model.event.Event;
import ru.explore.with.me.model.participation.Participation;
import ru.explore.with.me.model.user.User;
import ru.explore.with.me.repository.event.EventRepository;
import ru.explore.with.me.repository.participation.ParticipationRepository;
import ru.explore.with.me.repository.user.UserRepository;
import ru.explore.with.me.util.EventStatus;
import ru.explore.with.me.util.ParticipantStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbParticipationService implements ParticipationService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;
    private final ParticipationMapper participationMapper;

    public DbParticipationService(UserRepository userRepository,
                                  EventRepository eventRepository,
                                  ParticipationRepository participationRepository,
                                  ParticipationMapper participationMapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.participationRepository = participationRepository;
        this.participationMapper = participationMapper;
    }

    /**
     * Получение владельцем события списка заявок на участие в нем.
     *
     * @param userId  Id владельца
     * @param eventId Id События
     * @return List<ParticipationRequestDto>
     */
    @Override
    public List<ParticipationRequestDto> getParticipantsOfEvent(long userId, long eventId) {
        checkUserExist(userId);
        checkEventExist(eventId);

        return participationRepository.findAllByEventId(eventId).stream()
                .map(participationMapper::toParticipationDto)
                .collect(Collectors.toList());
    }

    /**
     * Cогласование участия в событии.
     * Если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
     * Если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки
     * необходимо отклонить
     *
     * @param userId  Id владельца
     * @param eventId Id События
     * @param partId  Id Заявки на участие
     * @param agree   согласие или отказ в участии
     * @return ParticipationRequestDto
     */
    @Override
    public ParticipationRequestDto setParticipationStatus(long userId, long eventId, long partId, boolean agree) {
        checkUserExist(userId);
        checkEventExist(eventId);

        Participation participation = getParticipationFromDb(partId);

        Event event = participation.getEvent();
        int participantsFact = participationRepository.getSumByEventIdAndStatusIs(
                eventId, ParticipantStatus.CONFIRMED.toString()).orElse(0);

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ValidationException("Модерация запросов на участия к данному событию не требуется");
        }

        // Если владелец подтвержадет участие, задаем статус CONFIRMED
        if (agree) {
            if (event.getParticipantLimit() == participantsFact) {
                throw new ValidationException("Невозможно подтвердить участие, " +
                        "достигнуто максимальное количество подтвержденных участинков");
            }

            participation.setStatus(ParticipantStatus.CONFIRMED);

            // Если количество участников достигло максимума, с учетом текущего, то отклоняем все оставшиеся
            if (event.getParticipantLimit() == participantsFact - 1) {
                participationRepository.changeParticipantsStatusOfEvent(
                        eventId, ParticipantStatus.CANCELED.toString(), ParticipantStatus.PENDING.toString());
            }

        } else { // Иначе задаем статус REJECTED
            participation.setStatus(ParticipantStatus.REJECTED);
        }

        participationRepository.save(participation);

        return participationMapper.toParticipationDto(participation);
    }

    @Override
    public List<ParticipationRequestDto> getUserParticipation(long userId) {
        checkUserExist(userId);
        return participationRepository.findAllByUserId(userId).stream()
                .map(participationMapper::toParticipationDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавление нового запроса на участие в событии.
     * Нельзя добавить повторный запрос
     * Инициатор события не может добавить запрос на участие в своём событии
     * Нельзя участвовать в неопубликованном событии
     * Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
     * Если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти
     * в состояние подтвержденного
     *
     * @param userId  id пользователя
     * @param eventId id события
     * @return ParticipationRequestDto
     */
    @Override
    public ParticipationRequestDto newParticipation(long userId, long eventId) {
        if (participationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new ValidationException("Заявка на участие уже существует");
        }

        User user = getUserFormDb(userId);
        Event event = getEventFromDb(eventId);

        if (event.getCreator().getId() == userId) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии");
        } else if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        } else if (event.getParticipantLimit() >
                0 && event.getParticipantLimit() == participationRepository.getSumByEventIdAndStatusIs(
                eventId, ParticipantStatus.CONFIRMED.toString()).orElse(0)) {
            throw new ValidationException("Лимит запросов на участие превышен");
        }

        Participation participation = new Participation(
                null,
                user,
                event,
                ParticipantStatus.PENDING,
                LocalDateTime.now());

        if (!event.isRequestModeration()) {
            participation.setStatus(ParticipantStatus.CONFIRMED);
        }

        return participationMapper.toParticipationDto(participationRepository.save(participation));
    }

    @Override
    public ParticipationRequestDto cancelParticipation(long userId, long requestId) {
        checkUserExist(userId);

        Participation participation = getParticipationFromDb(requestId);

        if (participation.getUser().getId() != userId) {
            throw new ValidationException("Отменить участие может только владелец запроса");
        }

        participation.setStatus(ParticipantStatus.CANCELED);

        return participationMapper.toParticipationDto(participationRepository.save(participation));
    }

    private void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private User getUserFormDb(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
    }

    private Participation getParticipationFromDb(long requestId) {
        return participationRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос на участие в событии не найден"));
    }

    private void checkEventExist(long eventId) {
        if (!eventRepository.existById(eventId)) {
            throw new NotFoundException("событие не найдено");
        }
    }

    private Event getEventFromDb(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не найдено"));
    }
}


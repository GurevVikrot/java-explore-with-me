package ru.explore.with.me.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Базовый клиент RestTemplate
 */
@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);

    }

    /**
     * Метод создания запроса к стороннему сервису.
     *
     * @param method     метод Http запроса
     * @param path       uri по которому нужно обращаться
     * @param parameters Параметры запроса. Может быть null
     * @param body       Тело запроса. Может быть null
     * @return ResponseEntity Object
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);

        ResponseEntity<Object> serverResponse;

        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (Exception e) {
            log.error("Ошибка запроса к сервису статистики", e);
            return null;
        }

        return prepareResponse(serverResponse);
    }

    /**
     * Метод анализа ответа на запрос к стороннему сервису.
     * В случае неудачного запроса логирует его и возвращает null.
     * При обращении к сервису статистики неудачный ответ может возникать, если не найдено совпадения по обращению
     *
     * @param response Ответ от сервиса
     * @return ResponseEntity Object
     */
    private ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Запрос к сервису статистики успешен");
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        log.error("Ошибка запроса к сервису статистики {}", responseBuilder.build());
        return null;
    }
}

package ru.explore.with.me.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventViewStats {
    private String app;
    private String uri;
    private long hits;
}

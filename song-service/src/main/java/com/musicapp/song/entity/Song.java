package com.musicapp.song.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "songs")
public class Song {
    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(nullable = false, length = 100)
    private String album;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private String year;
}

package com.spring.issuesolving.jpa;

import com.spring.issuesolving.jpa.nplus1.entity.Artist;
import com.spring.issuesolving.jpa.nplus1.repository.ArtistRepository;
import com.spring.issuesolving.jpa.nplus1.entity.Song;
import com.spring.issuesolving.jpa.nplus1.repository.SongRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NPlus1Test {

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {

        // given
        Artist artist1 = new Artist("뉴진스");
        artistRepository.save(artist1);

        Artist artist2 = new Artist("Maroon 5");
        artistRepository.save(artist2);

        Song song1 = Song.builder().title("Hype boy").artist(artist1).build();
        songRepository.save(song1);

        Song song2 = Song.builder().title("Attention").artist(artist1).build();
        songRepository.save(song2);

        Song song3 = Song.builder().title("Memories").artist(artist2).build();
        songRepository.save(song3);

        entityManager.clear();
    }

    @Nested
    @DisplayName("CASE 1. FetchType.EAGER")
    class EAGER {

        @DisplayName("N+1 문제 발생")
        @Test
        @Transactional
        void occurJpaNPlus1_EAGER() {

            // when - JPA N+1 문제 발생: 조회는 1번 했지만 조회 쿼리는 3번 실행
            List<Artist> artists = artistRepository.findAll();

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 1. Fetch join")
        @Test
        @Transactional
        void solveJpaNPlus1_FetchJoin() {

            // when
            List<Artist> artists = artistRepository.findAllJoinFetch();

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 2. EntityGraph")
        @Test
        @Transactional
        void solveJpaNPlus1_EntityGraph() {

            // when
            List<Artist> artists = artistRepository.findAllEntityGraph();

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 3. FetchMode.SUBSELECT")
        @Test
        @Transactional
        void solveJpaNPlus1_SUBSELECT() {

            // when
            List<Artist> artists = artistRepository.findAll();

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 4. BatchSize")
        @Test
        @Transactional
        void solveJpaNPlus1_BATCHSIZE() {

            // when
            List<Artist> artists = artistRepository.findAll();

            // then
            assertThat(artists.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("CASE 2. FetchType.LAZY")
    class LAZY {

        @DisplayName("N+1 문제 발생")
        @Test
        @Transactional
        void occurJpaNPlus1_LAZY() {
            // when
            List<Artist> artists = artistRepository.findAll();
            for (Artist artist : artists) {
                System.out.println("================ N+1 problem occur ================");
                artist.getSongs().forEach(song -> System.out.println(song.getTitle()));
            }

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 1. Fetch join")
        @Test
        @Transactional
        void solveJpaNPlus1_FetchJoin() {

            // when
            List<Artist> artists = artistRepository.findAllJoinFetch();
            for (Artist artist : artists) {
                artist.getSongs().forEach(song -> System.out.println(song.getTitle()));
            }

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 2. EntityGraph")
        @Test
        @Transactional
        void solveJpaNPlus1_EntityGraph() {

            // when
            List<Artist> artists = artistRepository.findAllEntityGraph();
            for (Artist artist : artists) {
                artist.getSongs().forEach(song -> System.out.println(song.getTitle()));
            }

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 3. FetchMode.SUBSELECT")
        @Test
        @Transactional
        void solveJpaNPlus1_SUBSELECT() {

            // when
            List<Artist> artists = artistRepository.findAll();
            for (Artist artist : artists) {
                artist.getSongs().forEach(song -> System.out.println(song.getTitle()));
            }

            // then
            assertThat(artists.size()).isEqualTo(2);
        }

        @DisplayName("해결방안 4. BatchSize")
        @Test
        @Transactional
        void solveJpaNPlus1_BATCHSIZE() {

            // when
            List<Artist> artists = artistRepository.findAll();
            for (Artist artist : artists) {
                artist.getSongs().forEach(song -> System.out.println(song.getTitle()));
            }

            // then
            assertThat(artists.size()).isEqualTo(2);
        }
    }
}

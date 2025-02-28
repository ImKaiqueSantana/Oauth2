package com.GrupoSv.springsecurity.repository;

import com.GrupoSv.springsecurity.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TweetRepository extends JpaRepository <Tweet, UUID> {
}

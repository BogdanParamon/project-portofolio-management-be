package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {

    boolean existsByProjectProjectIdAndUrl (UUID projectId, String url);

    List<Link> findAllByProjectProjectId (UUID projectId);
    List<Link> findAllByLinkId (UUID linkId);
}

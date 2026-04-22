package com.andre_nathan.gym_webservice.trainer.application.port.out;

import com.andre_nathan.gym_webservice.trainer.domain.model.Trainer;
import com.andre_nathan.gym_webservice.trainer.domain.model.TrainerId;

import java.util.List;
import java.util.Optional;

public interface TrainerRepositoryPort {
    Trainer save(Trainer trainer);
    Optional<Trainer> findById(TrainerId trainerId);
    List<Trainer> findAll();
    boolean existsByEmail(String email);
    boolean existsByEmailExcludingId(String email, TrainerId trainerId);
    void deleteById(TrainerId trainerId);
}

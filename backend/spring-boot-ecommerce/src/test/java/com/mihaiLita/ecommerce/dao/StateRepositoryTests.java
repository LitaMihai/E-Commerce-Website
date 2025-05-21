package com.mihaiLita.ecommerce.dao;

import com.mihaiLita.ecommerce.entity.Country;
import com.mihaiLita.ecommerce.entity.State;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class StateRepositoryTests {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @DisplayName("findByCountryCode returns states for a valid country code")
    @Test
    void findByCountryCodeReturnsStatesForValidCountryCode() {
        List<State> states = stateRepository.findByCountryCode("BR");
        assertThat(states).isNotEmpty();
        assertThat(states.getFirst().getCountry().getCode()).isEqualTo("BR");
    }

    @DisplayName("findByCountryCode returns empty list for an invalid country code")
    @Test
    void findByCountryCodeReturnsEmptyListForInvalidCountryCode() {
        List<State> states = stateRepository.findByCountryCode("INVALID");
        assertThat(states).isEmpty();
    }

    @DisplayName("findByCountryCode handles null country code gracefully")
    @Test
    void findByCountryCodeHandlesNullCountryCodeGracefully() {
        List<State> states = stateRepository.findByCountryCode(null);
        assertThat(states).isEmpty();
    }

    @DisplayName("findById returns state when present")
    @Test
    void findByIdReturnsStateWhenPresent() {
        State state = new State();
        state.setName("TestState");

        Country country = new Country();
        countryRepository.save(country);

        state.setCountry(country);

        stateRepository.save(state);

        State found = stateRepository.findById(state.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("TestState");
    }

    @DisplayName("findAll returns all states")
    @Test
    void findAllReturnsAllStates() {
        Country country1 = new Country();
        Country country2 = new Country();
        countryRepository.save(country1);
        countryRepository.save(country2);

        State state1 = new State();
        State state2 = new State();
        state1.setCountry(country1);
        state2.setCountry(country2);

        stateRepository.save(state1);
        stateRepository.save(state2);

        List<State> states = stateRepository.findAll();
        assertThat(states).hasSizeGreaterThanOrEqualTo(2);
    }

    @DisplayName("save persists a new state")
    @Test
    void savePersistsNewState() {
        State state = new State();
        state.setName("TestState");

        Country country = new Country();
        countryRepository.save(country);

        state.setCountry(country);

        State saved = stateRepository.save(state);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("TestState");
    }

    @DisplayName("deleteById removes the state")
    @Test
    void deleteByIdRemovesState() {
        State state = new State();
        state.setName("TestState");

        Country country = new Country();
        countryRepository.save(country);

        state.setCountry(country);

        State saved = stateRepository.save(state);
        Integer id = saved.getId();

        stateRepository.deleteById(id);
        assertThat(stateRepository.findById(id)).isEmpty();
    }
}
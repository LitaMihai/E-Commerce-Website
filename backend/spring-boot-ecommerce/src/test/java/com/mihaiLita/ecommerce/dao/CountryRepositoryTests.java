package com.mihaiLita.ecommerce.dao;

import com.mihaiLita.ecommerce.entity.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CountryRepositoryTests {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Test
    void findAllCountriesReturnsNonEmptyListWhenCountriesExist() {
        List<Country> countries = countryRepository.findAll();
        assertThat(countries).isNotEmpty();
    }

    @Test
    void findAllCountriesReturnsEmptyListWhenNoCountriesExist() {
        stateRepository.deleteAll();
        countryRepository.deleteAll();
        List<Country> countries = countryRepository.findAll();
        assertThat(countries).isEmpty();
    }

    @Test
    void findCountryByIdReturnsCountryWhenIdExists() {
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");
        country = countryRepository.save(country);

        Country foundCountry = countryRepository.findById(country.getId()).orElse(null);
        assertThat(foundCountry).isNotNull();
        assertThat(foundCountry.getName()).isEqualTo("Test Country");
        assertThat(foundCountry.getCode()).isEqualTo("TC");
    }

    @Test
    void findCountryByIdReturnsNullWhenIdDoesNotExist() {
        Country foundCountry = countryRepository.findById(999).orElse(null);
        assertThat(foundCountry).isNull();
    }
}

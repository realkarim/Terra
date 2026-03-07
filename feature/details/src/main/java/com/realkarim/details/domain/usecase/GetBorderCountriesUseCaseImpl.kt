package com.realkarim.details.domain.usecase

import com.realkarim.country.repository.CountryRepository
import com.realkarim.domain.result.DomainOutcome
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetBorderCountriesUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository
) : GetBorderCountriesUseCase {

    override suspend fun invoke(alphaCodes: List<String>): Map<String, String> = coroutineScope {
        alphaCodes
            .map { code -> async { code to countryRepository.getCountryByAlphaCode(code) } }
            .awaitAll()
            .associate { (code, outcome) ->
                code to when (outcome) {
                    is DomainOutcome.Success -> outcome.data.name
                    else -> code
                }
            }
    }
}

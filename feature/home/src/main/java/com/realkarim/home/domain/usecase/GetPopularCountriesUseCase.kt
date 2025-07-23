package com.realkarim.home.domain.usecase

import com.realkarim.domain.Outcome
import com.realkarim.home.domain.model.Country
import com.realkarim.network.model.ErrorResponse

interface GetPopularCountriesUseCase {
    suspend operator fun invoke(): Outcome<List<Country>, ErrorResponse>
}
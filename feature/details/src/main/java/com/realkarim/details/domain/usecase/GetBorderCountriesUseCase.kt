package com.realkarim.details.domain.usecase

interface GetBorderCountriesUseCase {
    /** Returns a map of alpha-3 code → country name for each code in [alphaCodes].
     *  Falls back to the code itself if the lookup fails. */
    suspend operator fun invoke(alphaCodes: List<String>): Map<String, String>
}

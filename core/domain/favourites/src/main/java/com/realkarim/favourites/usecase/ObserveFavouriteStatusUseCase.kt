package com.realkarim.favourites.usecase

import kotlinx.coroutines.flow.Flow

interface ObserveFavouriteStatusUseCase {
    operator fun invoke(alphaCode: String): Flow<Boolean>
}

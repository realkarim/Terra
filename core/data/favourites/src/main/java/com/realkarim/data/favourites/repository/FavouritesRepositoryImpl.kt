package com.realkarim.data.favourites.repository

import com.realkarim.country.model.Country
import com.realkarim.data.favourites.db.FavouritesDao
import com.realkarim.data.favourites.db.mapper.toDomain
import com.realkarim.data.favourites.db.mapper.toEntity
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import com.realkarim.favourites.repository.FavouritesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesRepositoryImpl(
    private val dao: FavouritesDao,
) : FavouritesRepository {

    override suspend fun toggleFavourite(country: Country): Result<Unit, DomainError> {
        return try {
            if (dao.isFavourite(country.alphaCode)) {
                dao.delete(country.alphaCode)
            } else {
                dao.insert(country.toEntity())
            }
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(DomainError.Unexpected)
        }
    }

    override fun observeAllFavourites(): Flow<List<Country>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeIsFavourite(alphaCode: String): Flow<Boolean> =
        dao.observeIsFavourite(alphaCode)
}

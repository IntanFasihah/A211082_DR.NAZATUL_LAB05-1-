package com.example.a211082_drnazatul_lab05.data

class PlaceRepository(private val dao: PlaceDao) {

    val allPlaces = dao.getAllPlaces()

    suspend fun insert(name: String) {
        dao.insert(PlaceEntity(name = name))
    }

    suspend fun delete(place: PlaceEntity) {
        dao.delete(place)
    }
}
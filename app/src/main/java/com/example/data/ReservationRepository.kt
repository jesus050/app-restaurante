package com.example.data

import kotlinx.coroutines.flow.Flow

class ReservationRepository(private val reservationDao: ReservationDao) {
    val allReservations: Flow<List<Reservation>> = reservationDao.getAllReservations()

    suspend fun insert(reservation: Reservation): Long {
        return reservationDao.insertReservation(reservation)
    }

    suspend fun deleteById(id: Int) {
        reservationDao.deleteReservationById(id)
    }
}

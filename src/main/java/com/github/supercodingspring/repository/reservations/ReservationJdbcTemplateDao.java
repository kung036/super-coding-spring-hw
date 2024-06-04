package com.github.supercodingspring.repository.reservations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;

@Repository
public class ReservationJdbcTemplateDao implements ReservationRepository {

    private JdbcTemplate template;

    static RowMapper<Reservation> reservationRowMapper = (((rs, rowNum) ->
            new Reservation(
                    rs.getInt("reservation_id"),
                    rs.getInt("passenger_id"),
                    rs.getInt("airline_ticket_id"),
                    rs.getTimestamp("reserve_at").toLocalDateTime()
            )));

    public ReservationJdbcTemplateDao(@Qualifier("jdbcTemplate2") JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    @Override
    public Boolean saveReservation(Reservation reservation) {
        Integer rowNums = template.update("INSERT INTO reservation(passenger_id, airline_ticket_id, reservation_status, reserve_at) VALUES (? ,? , ?, ? )",
                                          reservation.getPassengerId(), reservation.getAirlineTicketId(), reservation.getReservationStatus(),
                                          new Date(Timestamp.valueOf(reservation.getReserveAt()).getTime()));
        return rowNums > 0;
    }

    @Override
    public Reservation findReservationById(Integer reservationId) {
        return template.queryForObject("SELECT * FROM reservation WHERE reservation_id = ?", reservationRowMapper, reservationId);
    }
}

package services;

import entities.Event;
import entities.Location;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService implements IService<Location> {
    private Connection cnx;

    public LocationService() {
        cnx = MyConnection.getInstance().getConnection();
    }

    @Override
    public void create(Location location) throws SQLException {
        String query = "INSERT INTO location (name, address, capacity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, location.getName());
            ps.setString(2, location.getAddress());
            ps.setInt(3, location.getCapacity());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Location location) throws SQLException {
        String query = "UPDATE location SET name = ?, address = ?, capacity = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, location.getName());
            ps.setString(2, location.getAddress());
            ps.setInt(3, location.getCapacity());
            ps.setInt(4, location.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Location location) throws SQLException {
        String query = "DELETE FROM location WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, location.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Location> readAll() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Location location = new Location(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getInt("capacity")
                );
                locations.add(location);
            }
        }
        return locations;
    }

    public Location readById(int id) throws SQLException {
        String query = "SELECT * FROM location WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Location(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getInt("capacity")
                    );
                }
            }
        }
        return null;
    }

    public List<Location> readByName(String keyword) throws SQLException {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location WHERE name LIKE ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Location location = new Location(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getInt("capacity")
                    );
                    locations.add(location);
                }
            }
        }
        return locations;
    }

    public List<Event> readAllForLocation(Location location) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE location_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, location.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("location_id"),
                            rs.getDate("date").toLocalDate()
                    );
                    events.add(event);
                }
            }
        }
        return events;
    }
}
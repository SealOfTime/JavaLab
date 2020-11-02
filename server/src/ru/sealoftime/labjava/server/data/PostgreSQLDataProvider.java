package ru.sealoftime.labjava.server.data;

import jdk.jshell.spi.ExecutionControl;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import ru.sealoftime.labjava.core.model.data.CollectionProvider;
import ru.sealoftime.labjava.core.model.data.DataProvider;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.server.DatabaseConnectionManager;
import ru.sealoftime.labjava.server.RemoteUserInterface;
import ru.sealoftime.labjava.server.ServerApplication;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostgreSQLDataProvider implements DataProvider {
    private static final String ADD =
            "WITH added AS (" +
                "INSERT INTO Space_Marines " +
                "(name, coordinates_x, coordinates_y, creationDate, health, category, weaponType, meleeWeapon, chapter_name, marinesCount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                "RETURNING spacemarine_id " +
            ") INSERT INTO ownerships (owner_id, posession_id) select ?, spacemarine_id from added";
    private static final String REMOVE_SINGLE = "DELETE FROM Space_Marines sm USING Ownerships o WHERE sm.spacemarine_id=o.posession_id AND sm.spacemarine_id = ? AND o.owner_id=?";

    private final Date creationDate;
    private final ConcurrentQueueDataProvider cache;
    private final DatabaseConnectionManager dcm;
    public PostgreSQLDataProvider(Date creationDate, PriorityQueue<SpaceMarine> data, DatabaseConnectionManager dcm){
        this.creationDate = creationDate;
        this.dcm = dcm;
        this.cache = new ConcurrentQueueDataProvider(creationDate, data);
    }

    @Override
    public String getType() {
        return "PostgreSQL";
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    private void fillInAddStatemnt(PreparedStatement stmt, SpaceMarine spaceMarine) throws SQLException{
        //stmt.setInt   (1,  spaceMarine.getId());
        stmt.setString   (1,  spaceMarine.getName());
        stmt.setFloat    (2,  spaceMarine.getCoordinates().getX());
        stmt.setLong     (3,  spaceMarine.getCoordinates().getY());
        stmt.setTimestamp(4,  new Timestamp(spaceMarine.getCreationDate().getTime()));
        stmt.setInt      (5,  spaceMarine.getHealth());
        stmt.setString   (6,  spaceMarine.getCategory() != null ? spaceMarine.getCategory().toString() : null);
        stmt.setString   (7,  spaceMarine.getWeaponType().toString());
        stmt.setString   (8,  spaceMarine.getMeleeWeapon().toString());
        stmt.setString   (9, spaceMarine.getChapter() != null ? spaceMarine.getChapter().getName() : null);
        if(spaceMarine.getChapter() != null)
            stmt.setLong (10, spaceMarine.getChapter().getMarinesCount());
        else
            stmt.setNull (10, Types.BIGINT);
        var ud = RemoteUserInterface.getSession();
        ServerApplication.logger.info("Adding an object by "+ud.toString() );
        var owner_id = dcm.checkCredentials(ud.getUsername(), ud.getPassword());
        stmt.setInt(11, owner_id);
    }
    @Override
    public boolean add(SpaceMarine spaceMarine) {
        if(RemoteUserInterface.getSession() == null)
            return false;
        return dcm.performQueryWithOneStatement(ADD, (stmt) -> {
            fillInAddStatemnt(stmt, spaceMarine);
            var modified = stmt.executeUpdate();
            //var rs = stmt.getGeneratedKeys();
            if (modified > 0) {
                //var spaceMarineId = rs.getInt(1);
                //spaceMarine.setId(spaceMarineId);
                this.cache.add(spaceMarine);
            }
            return modified;
        }) > 0;
    }

    @Override
    public boolean addAll(Collection<? extends SpaceMarine> c) {
        return dcm.performQueryWithOneStatement(ADD, (stmt)-> {
            var s = RemoteUserInterface.getSession();
            var ownerId = dcm.checkCredentials(s.getUsername(), s.getPassword());
            for (var sm : c) {
                fillInAddStatemnt(stmt, sm);
                stmt.addBatch();
            }
            int modified = stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            for(var sm : c){
                rs.next();
                sm.setId(rs.getInt(1));
            }
            if (modified > 0) {
                this.cache.addAll(c);
            }
            return modified;
        }) > 0;
    }

    @Override
    public boolean remove(Object o) {
        if(!(o instanceof SpaceMarine))
            return false;
        var sm = (SpaceMarine)o;
        return dcm.performQueryWithOneStatement(REMOVE_SINGLE, (stmt)->{
            var ud = RemoteUserInterface.getSession();
            var owner_id = dcm.checkCredentials(ud.getUsername(), ud.getPassword());
            stmt.setInt(1, sm.getId());
            stmt.setInt(2, owner_id);
            int modified = stmt.executeUpdate();
            System.out.println(modified);
            if (modified > 0)
                this.cache.remove(o);
            return modified;
        }) > 0;
    }

    @Override
    public boolean removeIf(Predicate<? super SpaceMarine> filter) {
        boolean result = false;
        for(var sm : this){
            if(filter.test(sm))
                result = result || this.remove(sm) ;
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        var statement = "DELETE FROM Space_Marines sm USING ownerships o WHERE sm.spacemarine_id=o.posession_id AND o.owner_id= ? AND sm.spacemarine_id IN "
            + c.stream()
                    .filter(o->o instanceof SpaceMarine)
                    .map(o->((SpaceMarine)o).getId().toString())
                    .collect(Collectors.joining(", ", "(", ")"));

        return dcm.performQueryWithOneStatement(statement, (stmt)->{
            var ud = RemoteUserInterface.getSession();
            var owner_id = dcm.checkCredentials(ud.getUsername(), ud.getPassword());
            stmt.setInt(1, owner_id);
            var modified = stmt.executeUpdate();
            if (modified > 0)
                this.cache.removeAll(c);
            return modified;
        }) > 0;
    }


    @Override
    @SneakyThrows
    public boolean retainAll(Collection<?> c) {
        throw new ExecutionControl.NotImplementedException("Мне было лень это делать");
    }

    @Override
    public int removeFirst() {
        var first = this.cache.getFirst();
        if(this.remove(first))
            return first.getId();
        else return -1;
    }

    @Override
    public void clear() {
        if(dcm.performQueryWithOneStatement("DELETE FROM Space_Marines", PreparedStatement::executeUpdate) > 0)
            this.cache.clear();
    }

    @Override
    public int size() { return cache.size(); }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return cache.contains(o);
    }

    @Override
    public Iterator<SpaceMarine> iterator() {
        final var cacheIter = cache.iterator();
        return new Iterator<SpaceMarine>() {
            public boolean hasNext() { return cacheIter.hasNext(); }
            public void    remove()  { PostgreSQLDataProvider.this.remove(lastRef); }

            SpaceMarine lastRef;
            public SpaceMarine next() {
                lastRef = cacheIter.next();
                return lastRef;
            }
        };
    }

    @Override
    public Object[] toArray() {  return cache.toArray(); }

    @Override
    public <T> T[] toArray(T[] a) { return cache.toArray(a); }

    @Override
    public boolean containsAll(Collection<?> c) {
        return cache.containsAll(c);
    }

}

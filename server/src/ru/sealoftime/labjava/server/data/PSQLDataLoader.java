package ru.sealoftime.labjava.server.data;

import ru.sealoftime.labjava.core.model.data.DataProvider;
import ru.sealoftime.labjava.core.model.data.concrete.*;
import ru.sealoftime.labjava.core.model.io.DataLoader;
import ru.sealoftime.labjava.core.util.FormattedString;
import ru.sealoftime.labjava.server.DatabaseConnectionManager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

public class PSQLDataLoader extends DataLoader<SpaceMarine> {
    private DatabaseConnectionManager dcm;
    public PSQLDataLoader(DatabaseConnectionManager dcm){
        this.dcm = dcm;
    }

    public DataProvider initDataProvider(){
        var result = new PriorityQueue<SpaceMarine>();
        this.load(result);
        return new PostgreSQLDataProvider(new Date(), result, dcm);
    }
    @Override
    public <C extends Collection<SpaceMarine>> List<FormattedString> load(C dest) {
        try(var con = dcm.connect();
            var stmt = con.prepareStatement("SELECT * FROM Space_Marines");
            var results=stmt.executeQuery()){
            while(results.next())
                dest.add(spaceMarineFromResultSet(results));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            this.dcm.createTables();
            this.load(dest);

        }
        return null;
    }

    private SpaceMarine spaceMarineFromResultSet(ResultSet results) throws SQLException{
        SpaceMarine sm = new SpaceMarine();
        var id = results.getInt("spacemarine_id");
        sm.setId(id);
        SpaceMarine.markIdUsed(id);

        sm.setName(results.getString("name"));

        Coordinates coords = new Coordinates();
        coords.setX(results.getFloat("coordinates_x"));
        if(!results.wasNull()) {
            coords.setY(results.getLong("coordinates_y"));
            sm.setCoordinates(coords);
        }
        else sm.setCoordinates(null);
        sm.setCreationDate(results.getTimestamp("creationDate"));
        sm.setHealth(results.getInt("health"));

        var category = results.getString("category");
        if(category == null)
            sm.setCategory(null);
        else {
            sm.setCategory(
                    AstartesCategory.valueOf(category)
            );
        }
        sm.setWeaponType(Weapon.valueOf(results.getString("weaponType")));
        sm.setMeleeWeapon(MeleeWeapon.valueOf(results.getString("meleeWeapon")));
        var chapter = new Chapter();
        chapter.setName(results.getString("chapter_name"));
        if(results.wasNull())
            sm.setChapter(null);
        else{
            chapter.setMarinesCount(results.getLong("marinesCount"));
            sm.setChapter(chapter);
        }
        return sm;
    }
}

package com.jg.cesaryjuanclaudio.repositories;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jg.cesaryjuanclaudio.entities.*;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;
import com.jg.cesaryjuanclaudio.utils.Converters;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Estudiante.class,
                Docente.class,
                Administrativo.class,
                Libro.class,
                Prestamo.class
        },
        version = 2,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EstudianteDao estudianteDao();
    public abstract DocenteDao docenteDao();
    public abstract AdministrativoDao adminDao();
    public abstract LibroDao libroDao();
    public abstract PrestamoDao prestamoDao();

    private static volatile AppDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "bibliotecaUPN_database.db")
                            // CORRECCIÓN AQUÍ: Le pasamos 'false' (recomendado) o 'true'
                            .fallbackToDestructiveMigration(false)
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Usamos el executor global en lugar de crear uno nuevo
                                    databaseWriteExecutor.execute(() ->
                                            poblarDatosIniciales(INSTANCE)
                                    );
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void poblarDatosIniciales(AppDatabase db) {

        // 5 Estudiantes
        db.estudianteDao().insert(new Estudiante("Ana",     "García",   "ana.garcia@upn.pe",   "123456", "E001", "Ingeniería de Sistemas"));
        db.estudianteDao().insert(new Estudiante("Bruno",   "López",    "bruno.lopez@upn.pe",  "123456", "E002", "Administración"));
        db.estudianteDao().insert(new Estudiante("Carla",   "Ríos",     "carla.rios@upn.pe",   "123456", "E003", "Contabilidad"));
        db.estudianteDao().insert(new Estudiante("Diego",   "Mamani",   "diego.mamani@upn.pe", "123456", "E004", "Derecho"));
        db.estudianteDao().insert(new Estudiante("Elena",   "Torres",   "elena.torres@upn.pe", "123456", "E005", "Psicología"));

        // 5 Docentes
        db.docenteDao().insert(new Docente("Fernando", "Vega",    "fernando.vega@upn.pe",    "123456", "D001", "Ingeniería"));
        db.docenteDao().insert(new Docente("Gloria",   "Huanca",  "gloria.huanca@upn.pe",    "123456", "D002", "Ciencias Empresariales"));
        db.docenteDao().insert(new Docente("Héctor",   "Paredes", "hector.paredes@upn.pe",   "123456", "D003", "Derecho"));
        db.docenteDao().insert(new Docente("Irene",    "Cáceres", "irene.caceres@upn.pe",    "123456", "D004", "Humanidades"));
        db.docenteDao().insert(new Docente("Jorge",    "Medina",  "jorge.medina@upn.pe",     "123456", "D005", "Ciencias de la Salud"));

        // 5 Administrativos
        db.adminDao().insert(new Administrativo("Karen",   "Soto",    "karen.soto@upn.pe",    "123456", "A001", "Biblioteca"));
        db.adminDao().insert(new Administrativo("Luis",    "Chávez",  "luis.chavez@upn.pe",   "123456", "A002", "Sistemas"));
        db.adminDao().insert(new Administrativo("María",   "Flores",  "maria.flores@upn.pe",  "123456", "A003", "Recursos Humanos"));
        db.adminDao().insert(new Administrativo("Nelson",  "Quiroz",  "nelson.quiroz@upn.pe", "123456", "A004", "Finanzas"));
        db.adminDao().insert(new Administrativo("Olivia",  "Benítez", "olivia.benitez@upn.pe","123456", "A005", "Secretaría"));

        // 10 Libros conocidos
        db.libroDao().insert(new Libro(null, "L001", "Clean Code",                    "Programación",  "Prentice Hall",  3, 3, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L002", "El Quijote",                    "Literatura",    "Planeta",        2, 2, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L003", "Sapiens",                       "Historia",      "Debate",         4, 4, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L004", "Introducción a Algoritmos",     "Programación",  "MIT Press",      2, 2, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L005", "El Príncipe",                   "Política",      "Alianza",        3, 3, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L006", "Administración",                "Gestión",       "Pearson",        5, 5, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L007", "Derecho Civil",                 "Derecho",       "Grijley",        2, 2, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L008", "Psicología General",            "Psicología",    "McGraw-Hill",    3, 3, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L009", "Contabilidad Financiera",       "Contabilidad",  "Cengage",        4, 4, EstadoLibro.DISPONIBLE));
        db.libroDao().insert(new Libro(null, "L010", "Design Patterns",               "Programación",  "Addison-Wesley", 2, 2, EstadoLibro.DISPONIBLE));
    }
}
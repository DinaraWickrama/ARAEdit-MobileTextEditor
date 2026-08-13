package com.example.mobiletexteditor.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile FileDao _fileDao;

  private volatile VersionDao _versionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `files` (`fileId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `displayName` TEXT NOT NULL, `uriString` TEXT NOT NULL, `isReadOnly` INTEGER NOT NULL, `lastOpenedAt` INTEGER NOT NULL, `encoding` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `versions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fileId` INTEGER NOT NULL, `versionNumber` INTEGER NOT NULL, `delta` TEXT NOT NULL, `versionName` TEXT, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`fileId`) REFERENCES `files`(`fileId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8e6c0d29b389dc575f0c4d444e17bced')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `files`");
        db.execSQL("DROP TABLE IF EXISTS `versions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFiles = new HashMap<String, TableInfo.Column>(6);
        _columnsFiles.put("fileId", new TableInfo.Column("fileId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFiles.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFiles.put("uriString", new TableInfo.Column("uriString", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFiles.put("isReadOnly", new TableInfo.Column("isReadOnly", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFiles.put("lastOpenedAt", new TableInfo.Column("lastOpenedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFiles.put("encoding", new TableInfo.Column("encoding", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFiles = new TableInfo("files", _columnsFiles, _foreignKeysFiles, _indicesFiles);
        final TableInfo _existingFiles = TableInfo.read(db, "files");
        if (!_infoFiles.equals(_existingFiles)) {
          return new RoomOpenHelper.ValidationResult(false, "files(com.example.mobiletexteditor.data.FileEntity).\n"
                  + " Expected:\n" + _infoFiles + "\n"
                  + " Found:\n" + _existingFiles);
        }
        final HashMap<String, TableInfo.Column> _columnsVersions = new HashMap<String, TableInfo.Column>(6);
        _columnsVersions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVersions.put("fileId", new TableInfo.Column("fileId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVersions.put("versionNumber", new TableInfo.Column("versionNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVersions.put("delta", new TableInfo.Column("delta", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVersions.put("versionName", new TableInfo.Column("versionName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVersions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVersions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVersions.add(new TableInfo.ForeignKey("files", "CASCADE", "NO ACTION", Arrays.asList("fileId"), Arrays.asList("fileId")));
        final HashSet<TableInfo.Index> _indicesVersions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVersions = new TableInfo("versions", _columnsVersions, _foreignKeysVersions, _indicesVersions);
        final TableInfo _existingVersions = TableInfo.read(db, "versions");
        if (!_infoVersions.equals(_existingVersions)) {
          return new RoomOpenHelper.ValidationResult(false, "versions(com.example.mobiletexteditor.data.VersionEntity).\n"
                  + " Expected:\n" + _infoVersions + "\n"
                  + " Found:\n" + _existingVersions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8e6c0d29b389dc575f0c4d444e17bced", "8f009a963329639c49a0e6655b6c4d30");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "files","versions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `files`");
      _db.execSQL("DELETE FROM `versions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FileDao.class, FileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VersionDao.class, VersionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FileDao fileDao() {
    if (_fileDao != null) {
      return _fileDao;
    } else {
      synchronized(this) {
        if(_fileDao == null) {
          _fileDao = new FileDao_Impl(this);
        }
        return _fileDao;
      }
    }
  }

  @Override
  public VersionDao versionDao() {
    if (_versionDao != null) {
      return _versionDao;
    } else {
      synchronized(this) {
        if(_versionDao == null) {
          _versionDao = new VersionDao_Impl(this);
        }
        return _versionDao;
      }
    }
  }
}

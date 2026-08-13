package com.example.mobiletexteditor.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VersionDao_Impl implements VersionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VersionEntity> __insertionAdapterOfVersionEntity;

  public VersionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVersionEntity = new EntityInsertionAdapter<VersionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `versions` (`id`,`fileId`,`versionNumber`,`delta`,`versionName`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VersionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFileId());
        statement.bindLong(3, entity.getVersionNumber());
        statement.bindString(4, entity.getDelta());
        if (entity.getVersionName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getVersionName());
        }
        statement.bindLong(6, entity.getTimestamp());
      }
    };
  }

  @Override
  public Object insert(final VersionEntity version, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVersionEntity.insertAndReturnId(version);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object allForFile(final long fileId,
      final Continuation<? super List<VersionEntity>> $completion) {
    final String _sql = "SELECT * FROM versions WHERE fileId = ? ORDER BY versionNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fileId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VersionEntity>>() {
      @Override
      @NonNull
      public List<VersionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "fileId");
          final int _cursorIndexOfVersionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "versionNumber");
          final int _cursorIndexOfDelta = CursorUtil.getColumnIndexOrThrow(_cursor, "delta");
          final int _cursorIndexOfVersionName = CursorUtil.getColumnIndexOrThrow(_cursor, "versionName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<VersionEntity> _result = new ArrayList<VersionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VersionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFileId;
            _tmpFileId = _cursor.getLong(_cursorIndexOfFileId);
            final int _tmpVersionNumber;
            _tmpVersionNumber = _cursor.getInt(_cursorIndexOfVersionNumber);
            final String _tmpDelta;
            _tmpDelta = _cursor.getString(_cursorIndexOfDelta);
            final String _tmpVersionName;
            if (_cursor.isNull(_cursorIndexOfVersionName)) {
              _tmpVersionName = null;
            } else {
              _tmpVersionName = _cursor.getString(_cursorIndexOfVersionName);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new VersionEntity(_tmpId,_tmpFileId,_tmpVersionNumber,_tmpDelta,_tmpVersionName,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object latestForFile(final long fileId,
      final Continuation<? super VersionEntity> $completion) {
    final String _sql = "SELECT * FROM versions WHERE fileId = ? ORDER BY versionNumber DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fileId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VersionEntity>() {
      @Override
      @Nullable
      public VersionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "fileId");
          final int _cursorIndexOfVersionNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "versionNumber");
          final int _cursorIndexOfDelta = CursorUtil.getColumnIndexOrThrow(_cursor, "delta");
          final int _cursorIndexOfVersionName = CursorUtil.getColumnIndexOrThrow(_cursor, "versionName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final VersionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFileId;
            _tmpFileId = _cursor.getLong(_cursorIndexOfFileId);
            final int _tmpVersionNumber;
            _tmpVersionNumber = _cursor.getInt(_cursorIndexOfVersionNumber);
            final String _tmpDelta;
            _tmpDelta = _cursor.getString(_cursorIndexOfDelta);
            final String _tmpVersionName;
            if (_cursor.isNull(_cursorIndexOfVersionName)) {
              _tmpVersionName = null;
            } else {
              _tmpVersionName = _cursor.getString(_cursorIndexOfVersionName);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new VersionEntity(_tmpId,_tmpFileId,_tmpVersionNumber,_tmpDelta,_tmpVersionName,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object latestVersionNumber(final long fileId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(versionNumber) FROM versions WHERE fileId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fileId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

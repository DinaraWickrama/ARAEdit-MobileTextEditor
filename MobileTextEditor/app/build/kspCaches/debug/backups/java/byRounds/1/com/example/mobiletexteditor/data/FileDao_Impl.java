package com.example.mobiletexteditor.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FileDao_Impl implements FileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FileEntity> __insertionAdapterOfFileEntity;

  private final EntityDeletionOrUpdateAdapter<FileEntity> __deletionAdapterOfFileEntity;

  private final EntityDeletionOrUpdateAdapter<FileEntity> __updateAdapterOfFileEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetReadOnly;

  private final SharedSQLiteStatement __preparedStmtOfTouch;

  public FileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFileEntity = new EntityInsertionAdapter<FileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `files` (`fileId`,`displayName`,`uriString`,`isReadOnly`,`lastOpenedAt`,`encoding`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FileEntity entity) {
        statement.bindLong(1, entity.getFileId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getUriString());
        final int _tmp = entity.isReadOnly() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getLastOpenedAt());
        statement.bindString(6, entity.getEncoding());
      }
    };
    this.__deletionAdapterOfFileEntity = new EntityDeletionOrUpdateAdapter<FileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `files` WHERE `fileId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FileEntity entity) {
        statement.bindLong(1, entity.getFileId());
      }
    };
    this.__updateAdapterOfFileEntity = new EntityDeletionOrUpdateAdapter<FileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `files` SET `fileId` = ?,`displayName` = ?,`uriString` = ?,`isReadOnly` = ?,`lastOpenedAt` = ?,`encoding` = ? WHERE `fileId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FileEntity entity) {
        statement.bindLong(1, entity.getFileId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getUriString());
        final int _tmp = entity.isReadOnly() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getLastOpenedAt());
        statement.bindString(6, entity.getEncoding());
        statement.bindLong(7, entity.getFileId());
      }
    };
    this.__preparedStmtOfSetReadOnly = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE files SET isReadOnly = ? WHERE fileId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfTouch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE files SET lastOpenedAt = ? WHERE fileId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final FileEntity file, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFileEntity.insertAndReturnId(file);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FileEntity file, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFileEntity.handle(file);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FileEntity file, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFileEntity.handle(file);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setReadOnly(final long fileId, final boolean readOnly,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetReadOnly.acquire();
        int _argIndex = 1;
        final int _tmp = readOnly ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, fileId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetReadOnly.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object touch(final long fileId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfTouch.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, fileId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfTouch.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FileEntity>> recentFiles() {
    final String _sql = "SELECT * FROM files ORDER BY lastOpenedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"files"}, new Callable<List<FileEntity>>() {
      @Override
      @NonNull
      public List<FileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "fileId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfUriString = CursorUtil.getColumnIndexOrThrow(_cursor, "uriString");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfLastOpenedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastOpenedAt");
          final int _cursorIndexOfEncoding = CursorUtil.getColumnIndexOrThrow(_cursor, "encoding");
          final List<FileEntity> _result = new ArrayList<FileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FileEntity _item;
            final long _tmpFileId;
            _tmpFileId = _cursor.getLong(_cursorIndexOfFileId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpUriString;
            _tmpUriString = _cursor.getString(_cursorIndexOfUriString);
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final long _tmpLastOpenedAt;
            _tmpLastOpenedAt = _cursor.getLong(_cursorIndexOfLastOpenedAt);
            final String _tmpEncoding;
            _tmpEncoding = _cursor.getString(_cursorIndexOfEncoding);
            _item = new FileEntity(_tmpFileId,_tmpDisplayName,_tmpUriString,_tmpIsReadOnly,_tmpLastOpenedAt,_tmpEncoding);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long fileId, final Continuation<? super FileEntity> $completion) {
    final String _sql = "SELECT * FROM files WHERE fileId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fileId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FileEntity>() {
      @Override
      @Nullable
      public FileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "fileId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfUriString = CursorUtil.getColumnIndexOrThrow(_cursor, "uriString");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfLastOpenedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastOpenedAt");
          final int _cursorIndexOfEncoding = CursorUtil.getColumnIndexOrThrow(_cursor, "encoding");
          final FileEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpFileId;
            _tmpFileId = _cursor.getLong(_cursorIndexOfFileId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpUriString;
            _tmpUriString = _cursor.getString(_cursorIndexOfUriString);
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final long _tmpLastOpenedAt;
            _tmpLastOpenedAt = _cursor.getLong(_cursorIndexOfLastOpenedAt);
            final String _tmpEncoding;
            _tmpEncoding = _cursor.getString(_cursorIndexOfEncoding);
            _result = new FileEntity(_tmpFileId,_tmpDisplayName,_tmpUriString,_tmpIsReadOnly,_tmpLastOpenedAt,_tmpEncoding);
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
  public Object findByUri(final String uriString,
      final Continuation<? super FileEntity> $completion) {
    final String _sql = "SELECT * FROM files WHERE uriString = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, uriString);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FileEntity>() {
      @Override
      @Nullable
      public FileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "fileId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfUriString = CursorUtil.getColumnIndexOrThrow(_cursor, "uriString");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfLastOpenedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastOpenedAt");
          final int _cursorIndexOfEncoding = CursorUtil.getColumnIndexOrThrow(_cursor, "encoding");
          final FileEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpFileId;
            _tmpFileId = _cursor.getLong(_cursorIndexOfFileId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpUriString;
            _tmpUriString = _cursor.getString(_cursorIndexOfUriString);
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final long _tmpLastOpenedAt;
            _tmpLastOpenedAt = _cursor.getLong(_cursorIndexOfLastOpenedAt);
            final String _tmpEncoding;
            _tmpEncoding = _cursor.getString(_cursorIndexOfEncoding);
            _result = new FileEntity(_tmpFileId,_tmpDisplayName,_tmpUriString,_tmpIsReadOnly,_tmpLastOpenedAt,_tmpEncoding);
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

package contractstudy.usage.collectContracts.cre.JavaCREExtractor.constants;

import android.content.ActivityNotFoundException;
import android.content.ReceiverCallNotAllowedException;
import android.content.res.Resources;
import android.database.CursorIndexOutOfBoundsException;
import android.database.StaleDataException;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteAccessPermException;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteDatatypeMismatchException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteMisuseException;
import android.database.sqlite.SQLiteOutOfMemoryException;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.database.sqlite.SQLiteTableLockedException;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.opengl.GLException;
import android.os.BadParcelableException;
import android.os.NetworkOnMainThreadException;
import android.os.ParcelFormatException;
import android.renderscript.RSDriverException;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RSInvalidStateException;
import android.renderscript.RSRuntimeException;
import android.util.AndroidRuntimeException;
import android.util.NoSuchPropertyException;
import android.util.TimeFormatException;
import android.view.InflateException;
import android.view.KeyCharacterMap;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.RemoteViews;
import contractstudy.constants.constraint.ConstraintType;
import org.w3c.dom.DOMException;
import org.w3c.dom.ls.LSException;

import java.io.UncheckedIOException;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.ReadPendingException;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.WritePendingException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.ClosedDirectoryStreamException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.InvalidPathException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.ReadOnlyFileSystemException;
import java.security.AccessControlException;
import java.security.InvalidParameterException;
import java.security.ProviderException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.zone.ZoneRulesException;
import java.util.ConcurrentModificationException;
import java.util.DuplicateFormatFlagsException;
import java.util.EmptyStackException;
import java.util.FormatFlagsConversionMismatchException;
import java.util.FormatterClosedException;
import java.util.IllegalFormatCodePointException;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatException;
import java.util.IllegalFormatFlagsException;
import java.util.IllegalFormatPrecisionException;
import java.util.IllegalFormatWidthException;
import java.util.InputMismatchException;
import java.util.MissingFormatArgumentException;
import java.util.MissingFormatWidthException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.UnknownFormatConversionException;
import java.util.UnknownFormatFlagsException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.PatternSyntaxException;

public class JavaLangCRE {

  /**
   * Note that this can yield false results of classes with this name are imported from packages other than java.lang !
   *
   * @param excName
   * @return
   */
  public static ConstraintType getPreconditionTypeFromExceptionName(String excName) {
    if (excName.endsWith(AndroidRuntimeException.class.getSimpleName())) {
      return ConstraintType.CREAndroidRuntimeException;
    } else if (excName.endsWith(AnnotationTypeMismatchException.class.getSimpleName())) {
      return ConstraintType.CREAnnotationTypeMismatchException;
    } else if (excName.endsWith(ArithmeticException.class.getSimpleName())) {
      return ConstraintType.CREArithmeticException;
    } else if (excName.endsWith(ArrayStoreException.class.getSimpleName())) {
      return ConstraintType.CREArrayStoreException;
    } else if (excName.endsWith(BufferOverflowException.class.getSimpleName())) {
      return ConstraintType.CREBufferOverflowException;
    } else if (excName.endsWith(BufferUnderflowException.class.getSimpleName())) {
      return ConstraintType.CREBufferUnderflowException;
    } else if (excName.endsWith(ClassCastException.class.getSimpleName())) {
      return ConstraintType.CREClassCastException;
    } else if (excName.endsWith(CompletionException.class.getSimpleName())) {
      return ConstraintType.CRECompletionException;
    } else if (excName.endsWith(ConcurrentModificationException.class.getSimpleName())) {
      return ConstraintType.CREConcurrentModificationException;
    } else if (excName.endsWith(DOMException.class.getSimpleName())) {
      return ConstraintType.CREDOMException;
    } else if (excName.endsWith(DateTimeException.class.getSimpleName())) {
      return ConstraintType.CREDateTimeException;
    } else if (excName.endsWith(EmptyStackException.class.getSimpleName())) {
      return ConstraintType.CREEmptyStackException;
    } else if (excName.endsWith(EnumConstantNotPresentException.class.getSimpleName())) {
      return ConstraintType.CREEnumConstantNotPresentException;
    } else if (excName.endsWith(FileSystemAlreadyExistsException.class.getSimpleName())) {
      return ConstraintType.CREFileSystemAlreadyExistsException;
    } else if (excName.endsWith(FileSystemNotFoundException.class.getSimpleName())) {
      return ConstraintType.CREFileSystemNotFoundException;
    } else if (excName.endsWith(IllegalArgumentException.class.getSimpleName())) {
      return ConstraintType.CREIllegalArgumentException;
    } else if (excName.endsWith(IllegalMonitorStateException.class.getSimpleName())) {
      return ConstraintType.CREIllegalMonitorStateException;
    } else if (excName.endsWith(IllegalStateException.class.getSimpleName())) {
      return ConstraintType.CREIllegalStateException;
    } else if (excName.endsWith(IncompleteAnnotationException.class.getSimpleName())) {
      return ConstraintType.CREIncompleteAnnotationException;
    } else if (excName.endsWith(IndexOutOfBoundsException.class.getSimpleName())) {
      return ConstraintType.CREIndexOutOfBoundsException;
    } else if (excName.endsWith(LSException.class.getSimpleName())) {
      return ConstraintType.CRELSException;
    } else if (excName.endsWith(MalformedParameterizedTypeException.class.getSimpleName())) {
      return ConstraintType.CREMalformedParameterizedTypeException;
    } else if (excName.endsWith(MalformedParametersException.class.getSimpleName())) {
      return ConstraintType.CREMalformedParametersException;
    } else if (excName.endsWith(MissingResourceException.class.getSimpleName())) {
      return ConstraintType.CREMissingResourceException;
    } else if (excName.endsWith(NegativeArraySizeException.class.getSimpleName())) {
      return ConstraintType.CRENegativeArraySizeException;
    } else if (excName.endsWith(NoSuchElementException.class.getSimpleName())) {
      return ConstraintType.CRENoSuchElementException;
    } else if (excName.endsWith(NullPointerException.class.getSimpleName())) {
      return ConstraintType.CRENullPointerException;
    } else if (excName.endsWith(ParcelFormatException.class.getSimpleName())) {
      return ConstraintType.CREParcelFormatException;
    } else if (excName.endsWith(ParseException.class.getSimpleName())) {
      return ConstraintType.CREParseException;
    } else if (excName.endsWith(ProviderException.class.getSimpleName())) {
      return ConstraintType.CREProviderException;
    } else if (excName.endsWith(ProviderNotFoundException.class.getSimpleName())) {
      return ConstraintType.CREProviderNotFoundException;
    } else if (excName.endsWith(RejectedExecutionException.class.getSimpleName())) {
      return ConstraintType.CRERejectedExecutionException;
    } else if (excName.endsWith(SQLException.class.getSimpleName())) {
      return ConstraintType.CRESQLException;
    } else if (excName.endsWith(SecurityException.class.getSimpleName())) {
      return ConstraintType.CRESecurityException;
    } else if (excName.endsWith(TypeNotPresentException.class.getSimpleName())) {
      return ConstraintType.CRETypeNotPresentException;
    } else if (excName.endsWith(UncheckedIOException.class.getSimpleName())) {
      return ConstraintType.CREUncheckedIOException;
    } else if (excName.endsWith(UndeclaredThrowableException.class.getSimpleName())) {
      return ConstraintType.CREUndeclaredThrowableException;
    } else if (excName.endsWith(UnsupportedOperationException.class.getTypeName())) {
      return ConstraintType.CREUnsupportedOperationException;
    } else if (excName.endsWith(WrongMethodTypeException.class.getSimpleName())) {
      return ConstraintType.CREWrongMethodTypeException;
    } else if (excName.endsWith(AcceptPendingException.class.getSimpleName())) {
      return ConstraintType.CREAcceptPendingException;
    } else if (excName.endsWith(AccessControlException.class.getSimpleName())) {
      return ConstraintType.CREAccessControlException;
    } else if (excName.endsWith(AlreadyBoundException.class.getSimpleName())) {
      return ConstraintType.CREAlreadyBoundException;
    } else if (excName.endsWith(AlreadyConnectedException.class.getSimpleName())) {
      return ConstraintType.CREAlreadyConnectedException;
    } else if (excName.endsWith(ArrayIndexOutOfBoundsException.class.getSimpleName())) {
      return ConstraintType.CREArrayIndexOutOfBoundsException;
    } else if (excName.endsWith(BadParcelableException.class.getSimpleName())) {
      return ConstraintType.CREBadParcelableException;
    } else if (excName.endsWith(CancellationException.class.getSimpleName())) {
      return ConstraintType.CRECancellationException;
    } else if (excName.endsWith(CancelledKeyException.class.getSimpleName())) {
      return ConstraintType.CRECancelledKeyException;
    } else if (excName.endsWith(ClosedDirectoryStreamException.class.getSimpleName())) {
      return ConstraintType.CREClosedDirectoryStreamException;
    } else if (excName.endsWith(ClosedFileSystemException.class.getSimpleName())) {
      return ConstraintType.CREClosedFileSystemException;
    } else if (excName.endsWith(ClosedSelectorException.class.getSimpleName())) {
      return ConstraintType.CREClosedSelectorException;
    } else if (excName.endsWith(ClosedWatchServiceException.class.getSimpleName())) {
      return ConstraintType.CREClosedWatchServiceException;
    } else if (excName.endsWith(ConnectionPendingException.class.getSimpleName())) {
      return ConstraintType.CREConnectionPendingException;
    } else if (excName.endsWith(DateTimeParseException.class.getSimpleName())) {
      return ConstraintType.CREDateTimeParseException;
    } else if (excName.endsWith(DirectoryIteratorException.class.getSimpleName())) {
      return ConstraintType.CREDirectoryIteratorException;
    } else if (excName.endsWith(DuplicateFormatFlagsException.class.getSimpleName())) {
      return ConstraintType.CREDuplicateFormatFlagsException;
    } else if (excName.endsWith(FormatFlagsConversionMismatchException.class.getSimpleName())) {
      return ConstraintType.CREFormatFlagsConversionMismatchException;
    } else if (excName.endsWith(FormatterClosedException.class.getSimpleName())) {
      return ConstraintType.CREFormatterClosedException;
    } else if (excName.endsWith(IllegalBlockingModeException.class.getSimpleName())) {
      return ConstraintType.CREIllegalBlockingModeException;
    } else if (excName.endsWith(IllegalChannelGroupException.class.getSimpleName())) {
      return ConstraintType.CREIllegalChannelGroupException;
    } else if (excName.endsWith(IllegalCharsetNameException.class.getSimpleName())) {
      return ConstraintType.CREIllegalCharsetNameException;
    } else if (excName.endsWith(IllegalFormatCodePointException.class.getSimpleName())) {
      return ConstraintType.CREIllegalFormatCodePointException;
    } else if (excName.endsWith(IllegalFormatConversionException.class.getSimpleName())) {
      return ConstraintType.CREIllegalFormatConversionException;
    } else if (excName.endsWith(IllegalFormatException.class.getSimpleName())) {
      return ConstraintType.CREIllegalFormatException;
    } else if (excName.endsWith(IllegalFormatFlagsException.class.getSimpleName())) {
      return ConstraintType.CREIllegalFormatFlagsException;
    } else if (excName.endsWith(IllegalFormatPrecisionException.class.getSimpleName())) {
      return ConstraintType.CREIllegalFormatPrecisionException;
    } else if (excName.endsWith(IllegalFormatWidthException.class.getSimpleName())) {
      return ConstraintType.CREIllegalFormatWidthException;
    } else if (excName.endsWith(IllegalSelectorException.class.getSimpleName())) {
      return ConstraintType.CREIllegalSelectorException;
    } else if (excName.endsWith(IllegalThreadStateException.class.getSimpleName())) {
      return ConstraintType.CREIllegalThreadStateException;
    } else if (excName.endsWith(InputMismatchException.class.getSimpleName())) {
      return ConstraintType.CREInputMismatchException;
    } else if (excName.endsWith(InvalidMarkException.class.getSimpleName())) {
      return ConstraintType.CREInvalidMarkException;
    } else if (excName.endsWith(InvalidParameterException.class.getSimpleName())) {
      return ConstraintType.CREInvalidParameterException;
    } else if (excName.endsWith(InvalidPathException.class.getSimpleName())) {
      return ConstraintType.CREInvalidPathException;
    } else if (excName.endsWith(MissingFormatArgumentException.class.getSimpleName())) {
      return ConstraintType.CREMissingFormatArgumentException;
    } else if (excName.endsWith(MissingFormatWidthException.class.getSimpleName())) {
      return ConstraintType.CREMissingFormatWidthException;
    } else if (excName.endsWith(NoConnectionPendingException.class.getSimpleName())) {
      return ConstraintType.CRENoConnectionPendingException;
    } else if (excName.endsWith(NonReadableChannelException.class.getSimpleName())) {
      return ConstraintType.CRENonReadableChannelException;
    } else if (excName.endsWith(NonWritableChannelException.class.getSimpleName())) {
      return ConstraintType.CRENonWritableChannelException;
    } else if (excName.endsWith(NotYetBoundException.class.getSimpleName())) {
      return ConstraintType.CRENotYetBoundException;
    } else if (excName.endsWith(NotYetConnectedException.class.getSimpleName())) {
      return ConstraintType.CRENotYetConnectedException;
    } else if (excName.endsWith(NumberFormatException.class.getSimpleName())) {
      return ConstraintType.CRENumberFormatException;
    } else if (excName.endsWith(OverlappingFileLockException.class.getSimpleName())) {
      return ConstraintType.CREOverlappingFileLockException;
    } else if (excName.endsWith(PatternSyntaxException.class.getSimpleName())) {
      return ConstraintType.CREPatternSyntaxException;
    } else if (excName.endsWith(ProviderMismatchException.class.getSimpleName())) {
      return ConstraintType.CREProviderMismatchException;
    } else if (excName.endsWith(ReadOnlyBufferException.class.getSimpleName())) {
      return ConstraintType.CREReadOnlyBufferException;
    } else if (excName.endsWith(ReadOnlyFileSystemException.class.getSimpleName())) {
      return ConstraintType.CREReadOnlyFileSystemException;
    } else if (excName.endsWith(ReadPendingException.class.getSimpleName())) {
      return ConstraintType.CREReadPendingException;
    } else if (excName.endsWith(ShutdownChannelGroupException.class.getSimpleName())) {
      return ConstraintType.CREShutdownChannelGroupException;
    } else if (excName.endsWith(StringIndexOutOfBoundsException.class.getSimpleName())) {
      return ConstraintType.CREStringIndexOutOfBoundsException;
    } else if (excName.endsWith(UnknownFormatConversionException.class.getSimpleName())) {
      return ConstraintType.CREUnknownFormatConversionException;
    } else if (excName.endsWith(UnknownFormatFlagsException.class.getSimpleName())) {
      return ConstraintType.CREUnknownFormatFlagsException;
    } else if (excName.endsWith(UnresolvedAddressException.class.getSimpleName())) {
      return ConstraintType.CREUnresolvedAddressException;
    } else if (excName.endsWith(UnsupportedAddressTypeException.class.getSimpleName())) {
      return ConstraintType.CREUnsupportedAddressTypeException;
    } else if (excName.endsWith(UnsupportedCharsetException.class.getSimpleName())) {
      return ConstraintType.CREUnsupportedCharsetException;
    } else if (excName.endsWith(UnsupportedTemporalTypeException.class.getSimpleName())) {
      return ConstraintType.CREUnsupportedTemporalTypeException;
    } else if (excName.endsWith(WritePendingException.class.getSimpleName())) {
      return ConstraintType.CREWritePendingException;
    } else if (excName.endsWith(ZoneRulesException.class.getSimpleName())) {
      return ConstraintType.CREZoneRulesException;
    } else {
      return null;
    }
  }
}

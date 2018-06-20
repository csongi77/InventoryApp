package net.sytes.csongi.inventoryapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Null Entity class. This should be returned instead of null (null desing pattern)
 */
public final class NullEntity implements Entity {

    private long mErrorCode;

    /**
     * When creating a NullEntity we can pass an error code in this instance
     * in order for determining the cause of error.
     * @param errorCode - an error code.
     *                  @see {@link ErrorCodes}
     */
    public NullEntity(long errorCode) {
        this.mErrorCode = errorCode;
    }

    // Parcelable implementation
    protected NullEntity(Parcel in) {
        mErrorCode = in.readLong();
    }

    public static final Creator<NullEntity> CREATOR = new Creator<NullEntity>() {
        @Override
        public NullEntity createFromParcel(Parcel in) {
            return new NullEntity(in);
        }

        @Override
        public NullEntity[] newArray(int size) {
            return new NullEntity[size];
        }
    };

    // overriding Entity getId method
    @Override
    public long getId() {
        return mErrorCode;
    }

    @Override
    public void setId(long id) {
        mErrorCode=id;
    }

    // overriding Parcelable's methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mErrorCode);
    }
}

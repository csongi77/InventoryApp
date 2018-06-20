package net.sytes.csongi.inventoryapp.data;

import android.os.Parcel;

public class SupplierEntity implements Entity{

    private long mId;
    private String mSupplierName, mSupplierPhone;

    // An empty Ctor
    public SupplierEntity (){

    }

    // Parcelable implementation
    protected SupplierEntity(Parcel in) {
        mId = in.readLong();
        mSupplierName = in.readString();
        mSupplierPhone = in.readString();
    }

    public static final Creator<SupplierEntity> CREATOR = new Creator<SupplierEntity>() {
        @Override
        public SupplierEntity createFromParcel(Parcel in) {
            return new SupplierEntity(in);
        }

        @Override
        public SupplierEntity[] newArray(int size) {
            return new SupplierEntity[size];
        }
    };

    /**
     * getter and setter for Id.
     * @return long Id
     */
    @Override
    public long getId() {
        return mId;
    }

    @Override
    public void setId(long id) {
        mId=id;
    }

    /**
     * Getter and setter for Supplier name
     * @return name of Supplier (String)
     */
    public String getSupplierName() {
        return mSupplierName;
    }

    public void setSupplierName(String supplierName) {
        this.mSupplierName = supplierName;
    }

    /**
     * getter and setter for supplier phone number. Since there might be other characters than digits,
     * this field type is String
     * @return
     */
    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.mSupplierPhone = supplierPhone;
    }

    // overriding equals in order if we want remove this entity from List or database
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupplierEntity that = (SupplierEntity) o;

        if (mId != that.mId) return false;
        if (mSupplierName != null ? !mSupplierName.equals(that.mSupplierName) : that.mSupplierName != null)
            return false;
        return mSupplierPhone != null ? mSupplierPhone.equals(that.mSupplierPhone) : that.mSupplierPhone == null;
    }

    // if we want to override equals we must override hashCode (see Effective Java from Joshua Bloch)
    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + (mSupplierName != null ? mSupplierName.hashCode() : 0);
        result = 31 * result + (mSupplierPhone != null ? mSupplierPhone.hashCode() : 0);
        return result;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable implementation
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mSupplierName);
        dest.writeString(mSupplierPhone);
    }
}

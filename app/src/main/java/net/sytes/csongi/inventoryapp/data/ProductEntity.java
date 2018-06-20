package net.sytes.csongi.inventoryapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class for product
 */
<<<<<<< HEAD
public class ProductEntity implements Parcelable {
    private long mId;
    private int mPrice, mQuantity;
    private String mProductName, mSupplierName, mSupplierPhone;
=======
public class ProductEntity implements Entity {
    private long mId;
    private int mPrice, mQuantity;
    private String mProductName;
    private SupplierEntity mSupplierEntity;
>>>>>>> 76a1a2bc9c9c7bf36386fdb06f0ee4d4144cd711

    /**
     * An emtpy Ctor
     */
    public ProductEntity() {
    }

    /**
     * Parcelable implementation for this entity
     * @param in
     */
    protected ProductEntity(Parcel in) {
        mId = in.readLong();
        mPrice = in.readInt();
        mQuantity = in.readInt();
        mProductName = in.readString();
        mSupplierEntity = in.readParcelable(SupplierEntity.class.getClassLoader());
    }

    public static final Creator<ProductEntity> CREATOR = new Creator<ProductEntity>() {
        @Override
        public ProductEntity createFromParcel(Parcel in) {
            return new ProductEntity(in);
        }

        @Override
        public ProductEntity[] newArray(int size) {
            return new ProductEntity[size];
        }
    };

    /**
     * Getter for entity's Id
     * @return the id
     */
<<<<<<< HEAD
=======
    @Override
>>>>>>> 76a1a2bc9c9c7bf36386fdb06f0ee4d4144cd711
    public long getId() {
        return mId;
    }

<<<<<<< HEAD
    /**
     * Since only EntityManager can access to database and none of the clients can
     * set this value directly, the setter remains package private
     * @param id the id of Entity which was retreived from database
     */
    void setId(long id) {
        this.mId=id;
=======
    @Override
    public void setId(long id){
        mId=id;
>>>>>>> 76a1a2bc9c9c7bf36386fdb06f0ee4d4144cd711
    }

    /**
     * Getter and setter for product price
     * @return int value of price
     */
    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    /**
     * Getter and setter for Quantity of product
     * @return int value of quantity
     */
    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    /**
     * Getter and setter for product Name
     * @return String - name of product
     */
    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    /**
     * Getter and setter for Supplier
     * @return String - Name of supplier
     */
    public SupplierEntity getSupplierEntity() {
        return mSupplierEntity;
    }

    public void setSupplierEntity(SupplierEntity supplierEntity) {
        this.mSupplierEntity = supplierEntity;
    }

    // Overriding equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductEntity that = (ProductEntity) o;

        if (mId != that.mId) return false;
        if (mPrice != that.mPrice) return false;
        if (mQuantity != that.mQuantity) return false;
        if (mProductName != null ? !mProductName.equals(that.mProductName) : that.mProductName != null)
            return false;
        return mSupplierEntity != null ? mSupplierEntity.equals(that.mSupplierEntity) : that.mSupplierEntity == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + mPrice;
        result = 31 * result + mQuantity;
        result = 31 * result + (mProductName != null ? mProductName.hashCode() : 0);
        result = 31 * result + (mSupplierEntity != null ? mSupplierEntity.hashCode() : 0);
        return result;
    }

    /**
     * Parcelable implementation
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeInt(mPrice);
        dest.writeInt(mQuantity);
        dest.writeString(mProductName);
        dest.writeParcelable(mSupplierEntity, flags);
    }
}

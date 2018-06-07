package net.sytes.csongi.inventoryapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class for product
 */
public class ProductEntity implements Parcelable {
    private long mId;
    private int mPrice, mQuantity;
    private String mProductName;
    private SupplierEntity mSupplierEntity;

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
    public long getId() {
        return mId;
    }

    /**
     * Since only EntityManager can access to database and none of the clients can
     * set this value directly, the setter remains package private
     * @param id the id of Entity which was retreived from database
     */
    void setId(long id) {
        this.mId=id;
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

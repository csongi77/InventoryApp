package net.sytes.csongi.inventoryapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class for product
 */
public class ProductEntity implements Parcelable {
    private int mId, mPrice, mQuantity;
    private String mProductName, mSupplierName, mSupplierPhone;

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
        mId = in.readInt();
        mPrice = in.readInt();
        mQuantity = in.readInt();
        mProductName = in.readString();
        mSupplierName = in.readString();
        mSupplierPhone = in.readString();
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
    public int getId() {
        return mId;
    }

    /**
     * Since only EntityManager can access to database and none of the clients can
     * set this value directly, the setter remains package private
     * @param id the id of Entity which was retreived from database
     */
    void setId(int id) {
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
     * Getter and setter for Supplier Name
     * @return String - Name of supplier
     */
    public String getSupplierName() {
        return mSupplierName;
    }

    public void setSupplierName(String supplierName) {
        this.mSupplierName = supplierName;
    }

    /**
     * Getter and setter for Supplier phone. Since there might be other characters than digits,
     * this field type is String
     * @return String - Phone number of Supplier
     */
    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.mSupplierPhone = supplierPhone;
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
        dest.writeInt(mId);
        dest.writeInt(mPrice);
        dest.writeInt(mQuantity);
        dest.writeString(mProductName);
        dest.writeString(mSupplierName);
        dest.writeString(mSupplierPhone);
    }
}

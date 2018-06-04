package net.sytes.csongi.inventoryapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class for product
 */
public class ProductEntity implements Parcelable {
    private int mId, mPrice, mQuantity;
    private String mProductName, mSupplierName, mSupplierPhone;

    public ProductEntity() {
    }

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

    public int getId() {
        return mId;
    }

    void setId(int id) {
        this.mId=id;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public String getSupplierName() {
        return mSupplierName;
    }

    public void setSupplierName(String supplierName) {
        this.mSupplierName = supplierName;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.mSupplierPhone = supplierPhone;
    }

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

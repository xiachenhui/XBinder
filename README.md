# XBinder
简易版Binder客户端

- 先在目录中创建一个aidl目录，里面包名跟项目包名一致.
- 先创建以所需实体类名称为名的aidl文件，然后再类型改为parcelable
```
package com.example.xbinderservice;

parcelable User;
```
- 创建实体类java文件，实现parcelable
```
public class User implements Parcelable {
    private String name;
    private String password;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    protected User(Parcel in) {
        name = in.readString();
        password = in.readString();
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.password);
    }
}

```
- 再创建一个aidl文件，导入实体类，就可以对这个对象进行操作了
```
package com.example.xbinderservice;

// Declare any non-default types here with import statements
import com.example.xbinderservice.User;

interface XAidl {

  User login(in User user);

  long register(String  name,String password);

  int resetPwd(String name,String oldPwd,String newPwd);

}

```
- 在make project之后，在项目build文件夹下面的generated里面的aidl_source_output_dir目录里可以找到aidl文件

-keepnames class com.shelfmates.data.model.SaleInfo
-if class com.shelfmates.data.model.SaleInfo
-keep class com.shelfmates.data.model.SaleInfoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.SaleInfo
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.SaleInfo {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.Boolean,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}

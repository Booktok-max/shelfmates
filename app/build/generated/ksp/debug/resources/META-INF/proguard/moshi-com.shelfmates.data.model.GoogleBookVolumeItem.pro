-keepnames class com.shelfmates.data.model.GoogleBookVolumeItem
-if class com.shelfmates.data.model.GoogleBookVolumeItem
-keep class com.shelfmates.data.model.GoogleBookVolumeItemJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.GoogleBookVolumeItem
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.GoogleBookVolumeItem {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.String,com.shelfmates.data.model.VolumeInfo,com.shelfmates.data.model.SaleInfo,com.shelfmates.data.model.AccessInfo,int,kotlin.jvm.internal.DefaultConstructorMarker);
}

-keepnames class com.shelfmates.data.model.VolumeInfo
-if class com.shelfmates.data.model.VolumeInfo
-keep class com.shelfmates.data.model.VolumeInfoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.VolumeInfo
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.VolumeInfo {
    public synthetic <init>(java.lang.String,java.lang.String,java.util.List,java.lang.String,java.lang.String,java.lang.String,java.util.List,java.lang.Integer,java.lang.String,java.util.List,java.lang.Double,java.lang.Integer,java.lang.String,com.shelfmates.data.model.ImageLinks,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}

-keepnames class com.shelfmates.data.model.AccessInfo
-if class com.shelfmates.data.model.AccessInfo
-keep class com.shelfmates.data.model.AccessInfoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.AccessInfo
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.AccessInfo {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.Boolean,java.lang.Boolean,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}

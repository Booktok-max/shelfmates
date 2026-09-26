-keepnames class com.shelfmates.data.model.ImageLinks
-if class com.shelfmates.data.model.ImageLinks
-keep class com.shelfmates.data.model.ImageLinksJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.ImageLinks
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.ImageLinks {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}

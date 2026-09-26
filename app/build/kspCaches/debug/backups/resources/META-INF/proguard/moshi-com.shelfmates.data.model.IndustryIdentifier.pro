-keepnames class com.shelfmates.data.model.IndustryIdentifier
-if class com.shelfmates.data.model.IndustryIdentifier
-keep class com.shelfmates.data.model.IndustryIdentifierJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.IndustryIdentifier
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.IndustryIdentifier {
    public synthetic <init>(java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}

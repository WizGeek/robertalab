candle firmware.wxs java.wxs setup.wxs
light -out setup.msi -ext WixUIExtension -cultures:de-DE firmware.wixobj java.wixobj setup.wixobj -b ./firmware-1.2 -b ./java
@pause
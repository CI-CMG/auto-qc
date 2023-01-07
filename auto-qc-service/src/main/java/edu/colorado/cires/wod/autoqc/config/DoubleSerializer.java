package edu.colorado.cires.wod.autoqc.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleSerializer extends StdSerializer<Double> {

  public static final SimpleModule DOUBLE_SERIALIZER_MODULE;
  static {
    SimpleModule module = new SimpleModule("DoubleDecimal", Version.unknownVersion());
    module.addSerializer(Double.class, new DoubleSerializer());
    DOUBLE_SERIALIZER_MODULE = module;
  }

  public DoubleSerializer() {
    super(Double.class);
  }

  private static String doubleToString(double d) {
    DecimalFormat df = new DecimalFormat("0.######################", DecimalFormatSymbols.getInstance(Locale.US));
    return df.format(d);
  }

  @Override
  public void serialize(Double value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {

      gen.writeNumber(doubleToString(value));
    }
  };

}

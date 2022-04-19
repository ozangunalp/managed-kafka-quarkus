package com.ozangunalp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.azure.core.credential.TokenCredential;
import com.azure.core.util.serializer.TypeReference;
import com.azure.data.schemaregistry.SchemaRegistryClientBuilder;
import com.azure.data.schemaregistry.avro.SchemaRegistryAvroSerializer;
import com.azure.data.schemaregistry.avro.SchemaRegistryAvroSerializerBuilder;
import com.azure.identity.ClientSecretCredentialBuilder;

public class AzureKafkaAvroSerde implements Serializer<Object>, Deserializer<Object> {

    private SchemaRegistryAvroSerializer serializer;

    public AzureKafkaAvroSerde() {
    }

    String getStringProp(Map<String, ?> configs, String key) {
        return (String) configs.get("schema.registry." + key);
    }

    boolean getBooleanProp(Map<String, ?> configs, String key) {
        return Boolean.parseBoolean((String) configs.get("schema.registry." + key));
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        TokenCredential credential = new ClientSecretCredentialBuilder()
                .clientId(getStringProp(configs, "oauth.client.id"))
                .tenantId(getStringProp(configs, "oauth.tenant.id"))
                .clientSecret(getStringProp(configs, "oauth.client.secret"))
                .build();

        this.serializer = new SchemaRegistryAvroSerializerBuilder()
                .schemaRegistryAsyncClient(new SchemaRegistryClientBuilder()
                        .endpoint(getStringProp(configs, "url"))
                        .credential(credential)
                        .buildAsyncClient())
                .schemaGroup(getStringProp(configs, "schema.group"))
                .autoRegisterSchema(getBooleanProp(configs, "auto.register.schemas"))
                .avroSpecificReader(getBooleanProp(configs, "avro.specific.reader"))
                .buildSerializer();
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return serializer.deserialize(in, TypeReference.createInstance(Object.class));
    }

    @Override
    public byte[] serialize(String topic, Object record) {
        if (record == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(out, record);
        return out.toByteArray();
    }

    @Override
    public void close() {

    }
}

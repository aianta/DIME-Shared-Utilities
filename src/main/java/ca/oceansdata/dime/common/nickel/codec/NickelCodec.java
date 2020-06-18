package ca.oceansdata.dime.common.nickel.codec;

import ca.oceansdata.dime.common.nickel.Nickel;
import ca.oceansdata.dime.common.nickel.NickelOrigin;
import ca.oceansdata.dime.common.nickel.NickelType;
import ca.oceansdata.dime.common.nickel.impl.NickelImpl;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.UUID;

public class NickelCodec implements MessageCodec<NickelImpl, NickelImpl> {
    private static final Logger log = LoggerFactory.getLogger(NickelCodec.class);

    /** Nickels are encoded with fixed length fields first.
     *  Dynamic length fields are encoded with an int representing
     *  their size in bytes, followed by their data.
     *
     *  Order of encoding and the sizes in bytes (if fixed):
     *
     *  1)  timestamp (long - 8 bytes)
     *  2)  statusCode (int - 4 bytes)
     *  4)  correlationId
     *  5)  NickelType
     *  6)  orcid
     *  7)  NickelOrigin
     *  8)  httpResponseHeaders
     *  9)  tracing
     *  10) payload
     *
     *
     * @param buffer
     * @param nickel
     */
    @Override
    public void encodeToWire(Buffer buffer, NickelImpl nickel) {

        //Encode timestamp
        buffer.appendLong(nickel.timestamp());

        //Encode status code
        buffer.appendInt(nickel.statusCode());

        //Encode correlation Id
        encodeString(buffer, nickel.correlationId().toString());

        //Encode NickelType
        encodeString(buffer, nickel.type().name());

        //Encode orcid
        encodeString(buffer, nickel.orcid());

        //Encode NickelOrigin
        encodeString(buffer, nickel.origin().name());

        //Encode httpResponseHeaders
        encodeJson(buffer, nickel.httpResponseHeaders());

        //Encode requestQueryParameters
        encodeJson(buffer, nickel.requestQueryParams());

        //Encode tracing
        encodeJson(buffer, nickel.tracing());

        //Encode payload
        encodeBytes(buffer, nickel.getData());

    }

    @Override
    public NickelImpl decodeFromWire(int position, Buffer buffer) {

        //Encoded Nickel start position in buffer
        int _pos = position;

        //Decode timestamp
        long timestamp = buffer.getLong(_pos);

        //Increment position by 8 bytes
        _pos += 8;

        //Decode status code
        int statusCode = buffer.getInt(_pos);

        //Increment position by 4 bytes
        _pos += 4;

        //Decode correlation Id
        StringBuilder correlationIdBuilder = new StringBuilder();
        _pos = decodeString(correlationIdBuilder, buffer, _pos);
        UUID correlationId = UUID.fromString(correlationIdBuilder.toString());

        //Decode NickelType
        StringBuilder nickelTypeBuilder = new StringBuilder();
        _pos = decodeString(nickelTypeBuilder, buffer, _pos);
        NickelType nickelType = NickelType.valueOf(nickelTypeBuilder.toString());

        //Decode orcid
        StringBuilder orcidBuilder = new StringBuilder();
        _pos = decodeString(orcidBuilder, buffer, _pos);
        String orcid = orcidBuilder.toString();

        //Decode NickelOrigin
        StringBuilder originBuilder = new StringBuilder();
        _pos = decodeString(originBuilder, buffer, _pos);
        NickelOrigin origin = NickelOrigin.valueOf(originBuilder.toString());

        //Decode httpResponseHeaders
        JsonObject httpResponseHeaders = new JsonObject();
        _pos = decodeJson(httpResponseHeaders, buffer, _pos);

        //Decode requestQueryParamters
        JsonObject requestQueryParameters = new JsonObject();
        _pos = decodeJson(requestQueryParameters, buffer, _pos);

        //Decode tracing
        JsonObject tracing = new JsonObject();
        _pos = decodeJson(tracing, buffer, _pos);

        //Decode payload
        Buffer byteBuffer = Buffer.buffer();
        _pos = decodeBytes(byteBuffer, buffer, _pos);
        byte[] payload = byteBuffer.getBytes();

        NickelImpl decodedNickel = new NickelImpl();
        decodedNickel.setTimestamp(timestamp);
        decodedNickel.setStatusCode(statusCode);
        decodedNickel.setCorrelationId(correlationId);
        decodedNickel.setType(nickelType);
        decodedNickel.setOrcid(orcid);
        decodedNickel.setOrigin(origin);
        decodedNickel.setHttpResponseHeaders(httpResponseHeaders);
        decodedNickel.setRequestQueryParams(requestQueryParameters);
        decodedNickel.setTracing(tracing);
        decodedNickel.putData(payload);

        return decodedNickel;
    }

    @Override
    public NickelImpl transform(NickelImpl nickel) {

        //TODO-for testing only, send nickel back when done
        Buffer buffer = Buffer.buffer();
        encodeToWire(buffer, nickel);
        return decodeFromWire(0, buffer);
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

    private int decodeBytes(Buffer result, Buffer buffer, int position){
        //Get the number of bytes to read as data
        int numBytes = buffer.getInt(position);

        //Increment the position by 4 bytes (size of int)
        position +=4;

        //Get the specified number of bytes and append them to the result buffer
        result.appendBytes(buffer.getBytes(position, position+=numBytes));

        //Return the new buffer position
        return position;
    }

    private int decodeJson(JsonObject result, Buffer buffer, int position){
        //Get the length of the encoded json string in bytes
        int numBytes = buffer.getInt(position);

        //Increment the position by 4 bytes (size of int)
        position+=4;

        //Get the encoded json string
        String encodedJson = buffer.getString(position, position+=numBytes);

        //Decode the string into the json object
        result = new JsonObject(encodedJson);

        //Return the new buffer position
        return position;
    }

    /** Decode a variable length string by first reading
     *  its byte size as an int and then reading the data.
     * @param result a string builder to store the encoded string in once it's decoded
     * @param buffer buffer containing an encoded string
     * @param position the position on the buffer at which the string is located
     * @return the position in the buffer after the encoded string is read
     */
    private int decodeString(StringBuilder result,  Buffer buffer, int position){
        //Get the length of the encoded string in bytes
        int numBytes = buffer.getInt(position);

        //Increment the position by 4 bytes (size of int)
        position+=4;

        //Decode the string
        String decodedString = buffer.getString(position, position+=numBytes);

        //Append the decoded string to the string builder
        result.append(decodedString);

        //Return the new buffer position
        return position;
    }


    private void encodeString(Buffer buffer, String value){
        //Get the number of bytes in the string value
        int numBytes = value.getBytes().length;
        //Append string length to buffer
        buffer.appendInt(numBytes);
        //Append string to buffer
        buffer.appendString(value);
    }

    private void encodeJson(Buffer buffer,JsonObject data){
        //Get the string encoding of the json object
        String encodedJson = data.encode();

        //Get the number of bytes in the string encoded json
        int numBytes = encodedJson.getBytes().length;

        //Append byte size to buffer
        buffer.appendInt(numBytes);

        //Append encoded json to buffer
        buffer.appendString(encodedJson);
    }

    private void encodeBytes(Buffer buffer, byte[] data){
        //Get the length of bytes to encode
        int numBytes = data.length;

        //Append byte size to buffer
        buffer.appendInt(numBytes);

        //Append the bytes to the buffer
        buffer.appendBytes(data);
    }


}

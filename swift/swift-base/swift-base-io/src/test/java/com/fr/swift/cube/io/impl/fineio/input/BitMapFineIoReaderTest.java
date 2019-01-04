package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cube.io.input.ByteArrayReader;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ByteArrayFineIoReader.class, RoaringMutableBitMap.class, AllShowBitMap.class, RangeBitmap.class})
public class BitMapFineIoReaderTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteArrayFineIoReader.class);
        ByteArrayReader byteArrayReader = mock(ByteArrayReader.class);
        when(ByteArrayFineIoReader.build(Matchers.<URI>any())).thenReturn(byteArrayReader);

        when(byteArrayReader.get(anyLong())).thenReturn(
                new byte[]{BitMapType.ROARING_IMMUTABLE.getHead(), 1},
                new byte[]{BitMapType.ROARING_MUTABLE.getHead(), 1},
                new byte[]{BitMapType.ALL_SHOW.getHead(), 1},
                new byte[]{BitMapType.RANGE.getHead(), 1},
                new byte[]{BitMapType.BIT_SET_IMMUTABLE.getHead(), 1}
        );

        PowerMock.mockStaticPartial(RoaringMutableBitMap.class, "ofBytes");
        expect(RoaringMutableBitMap.ofBytes(EasyMock.<byte[]>anyObject(), EasyMock.anyInt(), EasyMock.anyInt())).andReturn(RoaringMutableBitMap.of()).anyTimes();

        PowerMock.mockStaticPartial(AllShowBitMap.class, "ofBytes");
        expect(AllShowBitMap.ofBytes(EasyMock.<byte[]>anyObject(), EasyMock.anyInt())).andReturn(AllShowBitMap.of(1));

        PowerMock.mockStaticPartial(RangeBitmap.class, "ofBytes");
        expect(RangeBitmap.ofBytes(EasyMock.<byte[]>anyObject(), EasyMock.anyInt())).andReturn(RangeBitmap.of(0, 1));

        PowerMock.replayAll();
    }

    @Test
    public void build() {
        BitMapFineIoReader.build(location);
    }

    @Test
    public void get() {
        Assert.assertTrue(BitMapFineIoReader.build(location).get(0).isEmpty());
        Assert.assertTrue(BitMapFineIoReader.build(location).get(1).isEmpty());
        Assert.assertTrue(BitMapFineIoReader.build(location).get(2).contains(0));
        Assert.assertTrue(BitMapFineIoReader.build(location).get(3).contains(0));

        try {
            BitMapFineIoReader.build(location).get(4);
            fail();
        } catch (Exception ig) {
            // 应该抛错
        }
    }

    @Test
    public void isReadable() {
        BitMapFineIoReader.build(location).isReadable();
    }

    @Test
    public void release() {
        BitMapFineIoReader.build(location).release();
    }
}
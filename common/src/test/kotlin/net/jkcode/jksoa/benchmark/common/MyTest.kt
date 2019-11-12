package net.jkcode.jksoa.benchmark.common

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.jkcode.jkmvc.common.randomInt
import net.jkcode.jksoa.benchmark.common.api.MessageEntity
import net.jkcode.jksoa.benchmark.common.impl.BenchmarkService
import org.junit.Test
import net.jkcode.jkmvc.serialize.ISerializer
import net.jkcode.jksoa.benchmark.common.impl.MessageModel
import net.jkcode.jksoa.common.RpcResponse
import java.io.File

/**
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-10-30 3:03 PM
 */
class MyTest {

    @Test
    fun testJson(){
        val msgs = ArrayList<MessageEntity>()
        for (i in 1..10) {
            val msg = MessageEntity()
            msg.id = i
            msg.fromUid = randomInt(10)
            msg.toUid = randomInt(10)
            msg.content = "hello orm"
            msgs.add(msg)
        }
        // list转json
        var json = JSON.toJSONString(msgs)
        println(json)
        println("-----------------")

        // json转array
        val msgs2 = JSONObject.parseArray(json, MessageEntity::class.java)
        println(msgs2)
    }

    @Test
    fun testService(){
        val service = BenchmarkService()
        val msgFuture = service.getMessageFromDb(1)
        println(msgFuture.get())
    }

    @Test
    fun testSerialize(){
        val i = 1
        val msg = MessageEntity()
        msg.id = i
        msg.fromUid = randomInt(10)
        msg.toUid = randomInt(10)
        msg.content = "benchmark message $i"

        val s = ISerializer.instance("fst")
        val bs = s.serialize(msg)

        val msg2 = s.unserialize(bs!!) as MessageEntity
        println(msg2)
    }

    @Test
    fun testByte(){
        val s = ISerializer.instance("fst")
        val msg = MessageModel.queryBuilder().where("id", "=", 1).findEntity<MessageModel, MessageEntity>()
        val res = RpcResponse(114137583746809856, msg)
        println(res)

        val bs = s.serialize(res)

        // server发送的
        val bs2 = ByteArray(160)
        bs2[0] = 0
        bs2[1] = 1
        bs2[2] = 35
        bs2[3] = 110
        bs2[4] = 101
        bs2[5] = 116
        bs2[6] = 46
        bs2[7] = 106
        bs2[8] = 107
        bs2[9] = 99
        bs2[10] = 111
        bs2[11] = 100
        bs2[12] = 101
        bs2[13] = 46
        bs2[14] = 106
        bs2[15] = 107
        bs2[16] = 115
        bs2[17] = 111
        bs2[18] = 97
        bs2[19] = 46
        bs2[20] = 99
        bs2[21] = 111
        bs2[22] = 109
        bs2[23] = 109
        bs2[24] = 111
        bs2[25] = 110
        bs2[26] = 46
        bs2[27] = 82
        bs2[28] = 112
        bs2[29] = 99
        bs2[30] = 82
        bs2[31] = 101
        bs2[32] = 115
        bs2[33] = 112
        bs2[34] = 111
        bs2[35] = 110
        bs2[36] = 115
        bs2[37] = 101
        bs2[38] = -126
        bs2[39] = 0
        bs2[40] = 0
        bs2[41] = -64
        bs2[42] = 22
        bs2[43] = -121
        bs2[44] = 127
        bs2[45] = -107
        bs2[46] = 1
        bs2[47] = -1
        bs2[48] = 0
        bs2[49] = 1
        bs2[50] = 51
        bs2[51] = 110
        bs2[52] = 101
        bs2[53] = 116
        bs2[54] = 46
        bs2[55] = 106
        bs2[56] = 107
        bs2[57] = 99
        bs2[58] = 111
        bs2[59] = 100
        bs2[60] = 101
        bs2[61] = 46
        bs2[62] = 106
        bs2[63] = 107
        bs2[64] = 115
        bs2[65] = 111
        bs2[66] = 97
        bs2[67] = 46
        bs2[68] = 98
        bs2[69] = 101
        bs2[70] = 110
        bs2[71] = 99
        bs2[72] = 104
        bs2[73] = 109
        bs2[74] = 97
        bs2[75] = 114
        bs2[76] = 107
        bs2[77] = 46
        bs2[78] = 99
        bs2[79] = 111
        bs2[80] = 109
        bs2[81] = 109
        bs2[82] = 111
        bs2[83] = 110
        bs2[84] = 46
        bs2[85] = 97
        bs2[86] = 112
        bs2[87] = 105
        bs2[88] = 46
        bs2[89] = 77
        bs2[90] = 101
        bs2[91] = 115
        bs2[92] = 115
        bs2[93] = 97
        bs2[94] = 103
        bs2[95] = 101
        bs2[96] = 69
        bs2[97] = 110
        bs2[98] = 116
        bs2[99] = 105
        bs2[100] = 116
        bs2[101] = 121
        bs2[102] = 4
        bs2[103] = -4
        bs2[104] = 5
        bs2[105] = 116
        bs2[106] = 111
        bs2[107] = 85
        bs2[108] = 105
        bs2[109] = 100
        bs2[110] = -10
        bs2[111] = 0
        bs2[112] = -4
        bs2[113] = 7
        bs2[114] = 102
        bs2[115] = 114
        bs2[116] = 111
        bs2[117] = 109
        bs2[118] = 85
        bs2[119] = 105
        bs2[120] = 100
        bs2[121] = -10
        bs2[122] = 9
        bs2[123] = -4
        bs2[124] = 2
        bs2[125] = 105
        bs2[126] = 100
        bs2[127] = -10
        bs2[128] = 1
        bs2[129] = -4
        bs2[130] = 7
        bs2[131] = 99
        bs2[132] = 111
        bs2[133] = 110
        bs2[134] = 116
        bs2[135] = 101
        bs2[136] = 110
        bs2[137] = 116
        bs2[138] = -4
        bs2[139] = 19
        bs2[140] = 98
        bs2[141] = 101
        bs2[142] = 110
        bs2[143] = 99
        bs2[144] = 104
        bs2[145] = 109
        bs2[146] = 97
        bs2[147] = 114
        bs2[148] = 107
        bs2[149] = 32
        bs2[150] = 109
        bs2[151] = 101
        bs2[152] = 115
        bs2[153] = 115
        bs2[154] = 97
        bs2[155] = 103
        bs2[156] = 101
        bs2[157] = 32
        bs2[158] = 49
        bs2[159] = 0

        // client收到的
        val bs3 = ByteArray(160)
        bs3[0] = 0
        bs3[1] = 1
        bs3[2] = 35
        bs3[3] = 110
        bs3[4] = 101
        bs3[5] = 116
        bs3[6] = 46
        bs3[7] = 106
        bs3[8] = 107
        bs3[9] = 99
        bs3[10] = 111
        bs3[11] = 100
        bs3[12] = 101
        bs3[13] = 46
        bs3[14] = 106
        bs3[15] = 107
        bs3[16] = 115
        bs3[17] = 111
        bs3[18] = 97
        bs3[19] = 46
        bs3[20] = 99
        bs3[21] = 111
        bs3[22] = 109
        bs3[23] = 109
        bs3[24] = 111
        bs3[25] = 110
        bs3[26] = 46
        bs3[27] = 82
        bs3[28] = 112
        bs3[29] = 99
        bs3[30] = 82
        bs3[31] = 101
        bs3[32] = 115
        bs3[33] = 112
        bs3[34] = 111
        bs3[35] = 110
        bs3[36] = 115
        bs3[37] = 101
        bs3[38] = -126
        bs3[39] = 0
        bs3[40] = 0
        bs3[41] = -64
        bs3[42] = 51
        bs3[43] = -127
        bs3[44] = -127
        bs3[45] = -107
        bs3[46] = 1
        bs3[47] = -1
        bs3[48] = 0
        bs3[49] = 1
        bs3[50] = 51
        bs3[51] = 110
        bs3[52] = 101
        bs3[53] = 116
        bs3[54] = 46
        bs3[55] = 106
        bs3[56] = 107
        bs3[57] = 99
        bs3[58] = 111
        bs3[59] = 100
        bs3[60] = 101
        bs3[61] = 46
        bs3[62] = 106
        bs3[63] = 107
        bs3[64] = 115
        bs3[65] = 111
        bs3[66] = 97
        bs3[67] = 46
        bs3[68] = 98
        bs3[69] = 101
        bs3[70] = 110
        bs3[71] = 99
        bs3[72] = 104
        bs3[73] = 109
        bs3[74] = 97
        bs3[75] = 114
        bs3[76] = 107
        bs3[77] = 46
        bs3[78] = 99
        bs3[79] = 111
        bs3[80] = 109
        bs3[81] = 109
        bs3[82] = 111
        bs3[83] = 110
        bs3[84] = 46
        bs3[85] = 97
        bs3[86] = 112
        bs3[87] = 105
        bs3[88] = 46
        bs3[89] = 77
        bs3[90] = 101
        bs3[91] = 115
        bs3[92] = 115
        bs3[93] = 97
        bs3[94] = 103
        bs3[95] = 101
        bs3[96] = 69
        bs3[97] = 110
        bs3[98] = 116
        bs3[99] = 105
        bs3[100] = 116
        bs3[101] = 121
        bs3[102] = 4
        bs3[103] = -4
        bs3[104] = 5
        bs3[105] = 116
        bs3[106] = 111
        bs3[107] = 85
        bs3[108] = 105
        bs3[109] = 100
        bs3[110] = -10
        bs3[111] = 0
        bs3[112] = -4
        bs3[113] = 7
        bs3[114] = 102
        bs3[115] = 114
        bs3[116] = 111
        bs3[117] = 109
        bs3[118] = 85
        bs3[119] = 105
        bs3[120] = 100
        bs3[121] = -10
        bs3[122] = 9
        bs3[123] = -4
        bs3[124] = 2
        bs3[125] = 105
        bs3[126] = 100
        bs3[127] = -10
        bs3[128] = 1
        bs3[129] = -4
        bs3[130] = 7
        bs3[131] = 99
        bs3[132] = 111
        bs3[133] = 110
        bs3[134] = 116
        bs3[135] = 101
        bs3[136] = 110
        bs3[137] = 116
        bs3[138] = -4
        bs3[139] = 19
        bs3[140] = 98
        bs3[141] = 101
        bs3[142] = 110
        bs3[143] = 99
        bs3[144] = 104
        bs3[145] = 109
        bs3[146] = 97
        bs3[147] = 114
        bs3[148] = 107
        bs3[149] = 32
        bs3[150] = 109
        bs3[151] = 101
        bs3[152] = 115
        bs3[153] = 115
        bs3[154] = 97
        bs3[155] = 103
        bs3[156] = 101
        bs3[157] = 32
        bs3[158] = 49
        bs3[159] = 0

        checkEquals(bs2, bs3)


        val res2 = s.unserialize(bs2!!) as RpcResponse
        println(res2)
        println(res.value == res2.value)

        val res3 = s.unserialize(bs3!!) as RpcResponse
        println(res3)
        println(res.value == res3.value)
    }

    private fun checkEquals(bs1: ByteArray, bs2: ByteArray) {
        println(bs1 == bs2)
        for (i in 0 until 160) {
            val v2 = bs1[i]
            val v3 = bs2[i]
            if (v2 != v3)
                println("第 $i 个字节不等: $v2 != $v3")
        }
    }
}
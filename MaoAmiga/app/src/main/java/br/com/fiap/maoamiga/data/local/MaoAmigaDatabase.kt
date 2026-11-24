package br.com.fiap.maoamiga.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PedidoAjudaEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MaoAmigaDatabase : RoomDatabase() {

    abstract fun pedidoAjudaDao(): PedidoAjudaDao

    companion object {
        @Volatile
        private var instance: MaoAmigaDatabase? = null

        fun getInstance(context: Context): MaoAmigaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MaoAmigaDatabase::class.java,
                    "mao_amiga.db"
                ).build().also { instance = it }
            }
    }
}

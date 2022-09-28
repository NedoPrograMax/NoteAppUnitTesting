package com.example.noteappunittesting.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteappunittesting.feature_note.data.data_source.NoteDatabase
import com.example.noteappunittesting.feature_note.data.repository.NoteRepoImpl
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo
import com.example.noteappunittesting.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase{
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteRepo(db: NoteDatabase): NoteRepo{
        return NoteRepoImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repo: NoteRepo): NoteUseCases{
        return NoteUseCases(
           getNotesUseCase = GetNotesUseCase(repo),
           deleteNoteUseCase = DeleteNoteUseCase(repo),
            addNoteUseCase = AddNoteUseCase(repo),
            getNoteUseCase = GetNoteUseCase(repo),
        )
    }
}
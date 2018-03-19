/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldwide.movie.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.worldwide.movie.data.Movie;
import java.util.List;

/**
 * Data Access Object for the tasks table.
 */
@Dao
public interface MoviesDao {

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Movie") List<Movie> getMovies();

    /**
     * Select a task by id.
     *
     * @param id the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Movie WHERE id = :id")
    Movie getMovieById(String id);

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param movie the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    /**
     * Update a task.
     *
     * @param movie task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    int updateMovie(Movie movie);


    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Movie WHERE id = :id")
    int deleteMovieById(String id);

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Movie")
    void deleteMovies();

}

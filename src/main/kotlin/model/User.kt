package model
import com.github.pgasync.Db
import com.github.pgasync.Row
import rx.Observable


/**
 * Represents a given User from the database
 */
class User(
    val id: Int? = null,
    val email: String,
    val password: String? = null) {



    /**
     *  Static context
     */
    companion object {

        /**
         * Constructs a user from a postgres Row
         */
        fun fromRow(row: Row) = User(row.getInt("id"), row.getString("email"))


        /**
         * Inserts this User into the database.
         * @return id of inserted user.
         */
        fun insert(db: Db, user: User): Observable<Int> {

            val query = "INSERT INTO \"user\" (email, password) VALUES (\$1, \$2) RETURNING id"
            return db
                .querySet(query, user.email, user.password)
                .map { it.row(0).getInt("id") }

        }

        /**
         * Gets a User with the specified ID.
         */
        fun selectFromId(db: Db, id: Int): Observable<User> {

            val query = "SELECT * FROM \"user\" WHERE id=\$1"
            return db
                .querySet(query, id)
                .map{ it.row(0) }
                .map{ fromRow(it) }
        }
    }
}
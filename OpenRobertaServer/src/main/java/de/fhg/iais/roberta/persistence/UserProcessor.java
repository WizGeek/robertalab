package de.fhg.iais.roberta.persistence;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.fhg.iais.roberta.persistence.bo.Role;
import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.dao.UserDao;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.persistence.util.HttpSessionState;
import de.fhg.iais.roberta.util.Key;

public class UserProcessor extends AbstractProcessor {

    public UserProcessor(DbSession dbSession, HttpSessionState httpSessionState) {
        super(dbSession, httpSessionState);
    }

    public User getUser(String account) {
        UserDao userDao = new UserDao(this.dbSession);
        User user = userDao.loadUser(account);
        if ( user != null ) {
            setSuccess(Key.USER_GET_ONE_SUCCESS);
            return user;
        } else {
            setError(Key.USER_GET_ONE_ERROR_ID_OR_PASSWORD_WRONG);
            return null;
        }
    }

    public User getUser(String account, String password) throws Exception {
        UserDao userDao = new UserDao(this.dbSession);
        User user = userDao.loadUser(account);
        if ( user != null && user.isPasswordCorrect(password) ) {
            setSuccess(Key.USER_GET_ONE_SUCCESS);
            return user;
        } else {
            setError(Key.USER_GET_ONE_ERROR_ID_OR_PASSWORD_WRONG);
            return null;
        }
    }

    public User getUserByEmail(String email) throws Exception {
        UserDao userDao = new UserDao(this.dbSession);
        User user = userDao.loadUserByEmail(email);
        if ( user != null ) {
            setSuccess(Key.USER_EMAIL_ONE_SUCCESS);
            return user;
        } else {
            setError(Key.USER_EMAIL_ONE_ERROR_USER_NOT_EXISTS_WITH_THIS_EMAIL);
            return null;
        }
    }

    public User getUser(int id) throws Exception {
        UserDao userDao = new UserDao(this.dbSession);
        User user = userDao.loadUser(id);
        if ( user != null ) {
            setSuccess(Key.USER_EMAIL_ONE_SUCCESS);
            return user;
        } else {
            setError(Key.USER_EMAIL_ONE_ERROR_USER_NOT_EXISTS_WITH_THIS_EMAIL);
            return null;
        }
    }

    public void createUser(String account, String password, String userName, String roleAsString, String email, String tags) throws Exception {
        if ( account == null || account.equals("") || password == null || password.equals("") ) {
            setError(Key.USER_CREATE_ERROR_MISSING_REQ_FIELDS, account);
        } else {
            UserDao userDao = new UserDao(this.dbSession);
            User user = userDao.persistUser(account, password, roleAsString);
            if ( user != null ) {
                setSuccess(Key.USER_CREATE_SUCCESS);
                user.setUserName(userName);
                user.setEmail(email);
                user.setTags(tags);
            } else {
                setError(Key.USER_CREATE_ERROR_NOT_SAVED_TO_DB, account);
            }
        }
    }

    public void updatePassword(String account, String oldPassword, String newPassword) throws Exception {
        if ( account == null || account.equals("") ) {
            setError(Key.USER_UPDATE_ERROR_ACCOUNT_WRONG, account);
        } else {
            User user = getUser(account, oldPassword);
            if ( user != null && this.httpSessionState.getUserId() == user.getId() ) {
                user.setPassword(newPassword);
                setSuccess(Key.USER_UPDATE_SUCCESS);
            } else {
                setError(Key.USER_UPDATE_ERROR_NOT_SAVED_TO_DB, account);
            }
        }
    }

    public void resetPassword(int userID, String newPassword) throws Exception {
        if ( userID <= 0 ) {
            setError(Key.USER_UPDATE_ERROR_ACCOUNT_WRONG, String.valueOf(userID));
        } else {
            User user = getUser(userID);
            if ( user != null ) {
                user.setPassword(newPassword);
                setSuccess(Key.USER_UPDATE_SUCCESS);
            } else {
                setError(Key.USER_UPDATE_ERROR_NOT_SAVED_TO_DB, String.valueOf(userID));
            }
        }
    }

    public void updateUser(String account, String userName, String roleAsString, String email, String tags) throws Exception {
        if ( account == null || account.equals("") ) {
            setError(Key.USER_UPDATE_ERROR_ACCOUNT_WRONG, account);
        } else {
            UserDao userDao = new UserDao(this.dbSession);
            User user = userDao.loadUser(account);
            if ( user != null && this.httpSessionState.getUserId() == user.getId() ) {
                user.setUserName(userName);
                user.setRole(Role.valueOf(roleAsString));
                user.setEmail(email);
                user.setTags(tags);
                setSuccess(Key.USER_UPDATE_SUCCESS);
            } else {
                setError(Key.USER_UPDATE_ERROR_NOT_SAVED_TO_DB, account);
            }
        }
    }

    public void deleteUser(String account, String password) throws Exception {
        UserDao userDao = new UserDao(this.dbSession);
        User user = userDao.loadUser(account);
        if ( user != null && user.isPasswordCorrect(password) ) {
            int rowCount = userDao.deleteUser(user);
            if ( rowCount > 0 ) {
                setSuccess(Key.USER_DELETE_SUCCESS);
            } else {
                setError(Key.USER_DELETE_ERROR_NOT_DELETED_IN_DB, account);
            }
        } else {
            setError(Key.USER_DELETE_ERROR_ID_NOT_FOUND, account);
        }
    }

    @Deprecated
    public JSONArray getUsers(String sortBy, int offset, String tagFilter) throws JSONException {
        UserDao userDao = new UserDao(this.dbSession);
        List<User> userList = userDao.loadUserList(sortBy, offset, tagFilter);
        JSONArray usersJSONArray = new JSONArray();

        for ( User user : userList ) {
            JSONObject userJSON = new JSONObject();
            if ( user != null ) {
                userJSON.put("id", user.getId());
                userJSON.put("name", user.getAccount());
                userJSON.put("role", user.getRole()); // This will be changed to user rights
                usersJSONArray.put(userJSON);
            }
        }
        setSuccess(Key.USER_GET_ALL_SUCCESS);
        return usersJSONArray;
    }
}

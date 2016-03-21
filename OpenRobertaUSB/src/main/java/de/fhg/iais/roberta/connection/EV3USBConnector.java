package de.fhg.iais.roberta.connection;

import java.io.IOException;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhg.iais.roberta.util.ORAtokenGenerator;

/**
 * Intended to be used as Singleton(!). This class handles two connections:</br>
 * robot<->USB program: {@link EV3Communicator}</br>
 * USB program<->Open Roberta server: {@link ServerCommunicator}</br>
 * After setting up an object of this class, you want to run this in a separate thread, because our protocol contains blocking http requests.
 * The state will be changed from the gui in another thread.
 *
 * @author dpyka
 * @see {@link Connector}
 */
public class EV3USBConnector extends Observable implements Runnable, Connector {

    private String brickIp = "10.0.1.1";
    private String serverIp = "localhost";
    private String serverPort = "1999";
    private final String serverAddress;

    private final EV3Communicator ev3comm;
    private ServerCommunicator servcomm;

    private JSONObject brickData = null;

    private static Logger log = Logger.getLogger("Connector");

    private State state = State.DISCOVER; // First state when program starts
    private String token = "";
    private boolean userDisconnect = false;

    private final String[] fwfiles = {
        "runtime",
        "shared",
        "jsonlib",
        "websocketlib",
        "ev3menu"
    };

    /**
     * Instantiate the connector with specific properties from the file or use default options defined in this class.
     * Set up a communicator to the EV3 and to the Open Roberta server.
     *
     * @param serverProps
     */
    public EV3USBConnector(ResourceBundle serverProps) {
        if ( serverProps != null ) {
            this.brickIp = serverProps.getString("brickIp");
            this.serverIp = serverProps.getString("serverIp");
            this.serverPort = serverProps.getString("serverPort");
        }
        this.serverAddress = this.serverIp + ":" + this.serverPort;

        log.config("Robot ip " + this.brickIp);

        this.ev3comm = new EV3Communicator(this.brickIp);

    }

    @Override
    public boolean findRobot() {
        try {
            if ( this.ev3comm.checkBrickState().equals("false") ) { // false ^= no program is running
                log.info("EV3 available");
                return true;
            } else {
                log.info("EV3 is executing a program");
                return false;
            }
        } catch ( IOException e ) {
            log.info("No EV3 device connected yet");
            return false;
        }
    }

    private void setupServerCommunicator() {
        this.servcomm = new ServerCommunicator(this.serverAddress);
    }

    @Override
    public void run() {
        log.config("Starting EV3 Connector Thread.");
        setupServerCommunicator();
        log.config("Server address " + this.serverAddress);
        while ( true ) {
            switch ( this.state ) {
                case DISCOVER:
                    try {
                        switch ( this.ev3comm.checkBrickState() ) {
                            case "true": // program is running
                                break;
                            case "false": // brick available and no program running
                                this.state = State.WAIT_FOR_CONNECT;
                                break;
                        }
                        Thread.sleep(1000);
                    } catch ( IOException | InterruptedException e ) {
                        log.info(State.DISCOVER + " " + e.getMessage());
                    }
                    notifyConnectionStateChanged(this.state);
                    break;
                case WAIT_EXECUTION:
                    this.state = State.WAIT_EXECUTION;
                    notifyConnectionStateChanged(this.state);
                    try {
                        switch ( this.ev3comm.checkBrickState() ) {
                            case "true": // program is running
                                this.state = State.WAIT_EXECUTION;
                                //notifyConnectionStateChanged(this.state);
                                break;
                            case "false": // brick available and no program running
                                log.info(State.WAIT_EXECUTION + "EV3 plugged in again, no program running, OK");
                                this.state = State.WAIT_FOR_CMD;
                                notifyConnectionStateChanged(this.state);
                                break;
                        }
                        Thread.sleep(1000);
                    } catch ( IOException | InterruptedException e ) {
                        log.info(State.WAIT_EXECUTION + " " + e.getMessage());
                    }
                    break;
                case WAIT_FOR_CONNECT:
                    try {
                        switch ( this.ev3comm.checkBrickState() ) {
                            case "true":
                                this.state = State.DISCOVER;
                                notifyConnectionStateChanged(State.DISCOVER);
                                break;
                            case "false":
                                // wait for user
                                break;
                            default:
                                break;
                        }
                        Thread.sleep(1000);
                    } catch ( IOException brickerror ) {
                        log.info(State.WAIT_FOR_CONNECT + " " + brickerror.getMessage());
                        reset(null);
                    } catch ( InterruptedException e ) {
                        // ok
                    }
                    break;
                case CONNECT:
                    this.token = ORAtokenGenerator.generateToken();
                    this.state = State.WAIT_FOR_SERVER;
                    notifyConnectionStateChanged(State.WAIT_FOR_SERVER);
                    try {
                        this.brickData = this.ev3comm.pushToBrick(CMD_REGISTER);
                        this.brickData.put(KEY_TOKEN, this.token);
                        this.brickData.put(KEY_CMD, CMD_REGISTER);
                    } catch ( IOException brickerror ) {
                        log.info(State.CONNECT + " " + brickerror.getMessage());
                        reset(State.ERROR_BRICK);
                        break;
                    }
                    try {
                        if ( this.state == State.DISCOVER ) {
                            log.info("User is clicking connect togglebutton too fast!");
                            break;
                        }
                        JSONObject serverResponse = this.servcomm.pushRequest(this.brickData);
                        String command = serverResponse.getString("cmd");
                        switch ( command ) {
                            case CMD_REPEAT:
                                try {
                                    this.brickData = this.ev3comm.pushToBrick(CMD_REPEAT);
                                } catch ( IOException brickerror ) {
                                    log.info(State.CONNECT + " " + brickerror.getMessage());
                                    reset(State.ERROR_BRICK);
                                    break;
                                }
                                this.state = State.WAIT_FOR_CMD;
                                notifyConnectionStateChanged(State.WAIT_FOR_CMD);
                                break;
                            case CMD_ABORT:
                                reset(State.TOKEN_TIMEOUT);
                                break;
                            default:
                                log.info(State.CONNECT + " Command " + command + " unknown");
                                reset(null);
                                break;
                        }
                    } catch ( IOException servererror ) {
                        log.info(State.CONNECT + " " + servererror.getMessage());
                        reset(State.ERROR_HTTP);
                    }
                    break;
                case WAIT_FOR_CMD:
                    try {
                        this.brickData = this.ev3comm.pushToBrick(CMD_REPEAT);
                        this.brickData.put(KEY_TOKEN, this.token);
                        this.brickData.put(KEY_CMD, CMD_PUSH);
                    } catch ( IOException brickerror ) {
                        log.info(State.WAIT_FOR_CMD + " " + brickerror.getMessage());
                        reset(State.ERROR_BRICK);
                        break;
                    }
                    String responseCommandFromServer = "default";
                    try {
                        responseCommandFromServer = this.servcomm.pushRequest(this.brickData).getString(KEY_CMD);
                    } catch ( IOException | JSONException servererror ) {
                        // continue to default block
                        log.info(State.WAIT_FOR_CMD + " Server response not ok " + servererror.getMessage());
                        reset(State.ERROR_HTTP);
                        break;
                    }
                    switch ( responseCommandFromServer ) {
                        case CMD_REPEAT:
                            break;
                        case CMD_ABORT:
                            try {
                                this.ev3comm.disconnectBrick();
                            } catch ( IOException brickerror ) {
                                log.info(State.WAIT_FOR_CMD + " Got" + CMD_ABORT + "and Brick disconnect failed " + brickerror.getMessage());
                            }
                            reset(null);
                            break;
                        case CMD_UPDATE:
                            log.info("Execute firmware update");
                            try {
                                for ( int i = 0; i < this.fwfiles.length; i++ ) {
                                    byte[] binaryfile = this.servcomm.downloadFirmwareFile(this.fwfiles[i]);
                                    this.ev3comm.uploadFirmwareFile(binaryfile, this.servcomm.getFilename());
                                }
                                this.ev3comm.restartBrick();
                                log.info("Firmware update successful. Restarting EV3 now!");
                                reset(null);
                                try {
                                    Thread.sleep(3000);
                                } catch ( InterruptedException e ) {
                                    // ok;
                                }
                            } catch ( IOException e ) {
                                log.info(State.WAIT_FOR_CMD + " Brick update failed " + e.getMessage());
                                reset(State.ERROR_UPDATE);
                            }
                            break;
                        case CMD_DOWNLOAD:
                            log.info("Download user program");
                            try {
                                byte[] binaryfile = this.servcomm.downloadProgram(this.brickData);
                                String filename = this.servcomm.getFilename();
                                this.ev3comm.uploadProgram(binaryfile, filename);
                                this.state = State.WAIT_EXECUTION;
                            } catch ( IOException e ) {
                                // do not give up the brick, try another push request
                                // user has to click on run button again
                                log.info(State.WAIT_FOR_CMD + " Downlaod file failed " + e.getMessage());
                                this.state = State.WAIT_FOR_CMD;
                            }
                            break;
                        case CMD_CONFIGURATION:
                            log.warning("Command " + responseCommandFromServer + " unused, ignore and continue push!");
                            break;
                        default:
                            log.warning("Command " + responseCommandFromServer + " unknown");
                            reset(null);
                            break;
                    }
                default:
                    break;
            }
        }
    }

    /**
     * Reset the USB program to the start state (discover).
     *
     * @param additionalerrormessage Display a popup with error message. If this is null, we do not want to display the tooltip.
     */
    private void reset(State additionalerrormessage) {
        if ( (!this.userDisconnect) && (additionalerrormessage != null) ) {
            notifyConnectionStateChanged(additionalerrormessage);
        }
        this.userDisconnect = false;
        this.state = State.DISCOVER;
        notifyConnectionStateChanged(this.state);
    }

    @Override
    public void updateCustomServerAddress(String customServerAddress) {
        this.servcomm.updateCustomServerAddress(customServerAddress);
        log.info("Now using custom address " + customServerAddress);
    }

    @Override
    public void resetToDefaultServerAddress() {
        this.servcomm.updateCustomServerAddress(this.serverAddress);
        log.info("Now using default address " + this.serverAddress);
    }

    @Override
    public void update() {
        this.state = State.UPDATE;
    }

    @Override
    public void connect() {
        this.state = State.CONNECT;
    }

    @Override
    public void disconnect() {
        log.info("DISCONNECTING by user");
        this.userDisconnect = true;
        try {
            this.ev3comm.disconnectBrick();
        } catch ( IOException e ) {
            // ok
        }
        this.servcomm.abort();
        notifyConnectionStateChanged(State.DISCOVER);
        this.state = State.DISCOVER;
    }

    @Override
    public void notifyConnectionStateChanged(State state) {
        setChanged();
        notifyObservers(state);
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public String getBrickName() {
        String brickname = this.brickData.getString("brickname");
        if ( brickname != null ) {
            return brickname;
        } else {
            return "";
        }
    }

    @Override
    public void close() {
        disconnect();
        this.servcomm.shutdown();
        this.ev3comm.shutdown();
    }
}

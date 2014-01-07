<?php

class Message
{
    private $id;
    private $user;
    private $device;
    private $lat;
    private $lon;
    private $datetime;
    private $mime;
    private $file;
    private $message;

    public function __construct($data)
    {
        $this->id = isset($data['id']) ? $data['id'] : null;
        $this->user = isset($data['user']) ? $data['user'] : null;
        $this->device = isset($data['device']) ? $data['device'] : null;
        $this->lat = isset($data['lat']) ? $data['lat'] : null;
        $this->lon = isset($data['lon']) ? $data['lon'] : null;
        $this->datetime = isset($data['datetime']) ? $data['datetime'] : null;
        $this->mime = isset($data['mime']) ? $data['mime'] : null;
        $this->file = isset($data['file']) ? $data['file'] : null;
        $this->message = isset($data['message']) ? $data['message'] : null;
		$this->parentt = isset($data['parent']) ? $data['parent'] : null;
    }

    public function getInfo()
    {
        return array('id' => $this->id,
            'user' => $this->user->getInfo(),
            'device' => $this->device->getInfo(),
			'parent' => $this->parentt,
            'lat' => $this->lat,
            'lon' => $this->lon,
            'datetime' => $this->datetime,
            'message' => $this->message,
            'mime' => $this->mime,
            'file' => $this->file);
    }

    /**
     * @return mixed
     */
    public function getDatetime()
    {
        return $this->datetime;
    }

    /**
     * @return mixed
     */
    public function getDevice()
    {
        return $this->device;
    }

    /**
     * @return mixed
     */
    public function getFile()
    {
        return $this->file;
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return mixed
     */
    public function getLat()
    {
        return $this->lat;
    }

    /**
     * @return mixed
     */
    public function getLon()
    {
        return $this->lon;
    }

    /**
     * @return mixed
     */
    public function getMime()
    {
        return $this->mime;
    }

	    /**
     * @return mixed
     */
    public function getParent()
    {
        return $this->parentt;
    }
	
    /**
     * @return mixed
     */
    public function getUser()
    {
        return $this->user;
    }

    /**
     * @param null $datetime
     */
    public function setDatetime($datetime)
    {
        $this->datetime = $datetime;
    }

    /**
     * @param null $device
     */
    public function setDevice($device)
    {
        $this->device = $device;
    }

    /**
     * @param null $file
     */
    public function setFile($file)
    {
        $this->file = $file;
    }

    /**
     * @param null $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @param null $lat
     */
    public function setLat($lat)
    {
        $this->lat = $lat;
    }

    /**
     * @param null $lon
     */
    public function setLon($lon)
    {
        $this->lon = $lon;
    }

    /**
     * @param null $mime
     */
    public function setMime($mime)
    {
        $this->mime = $mime;
    }

    /**
     * @param null $user
     */
    public function setUser($user)
    {
        $this->user = $user;
    }

	    /**
     * @param null $message
     */
    public function setParent($parent)
    {
        $this->parentt = $parent;
    }
	
    /**
     * @param null $message
     */
    public function setMessage($message)
    {
        $this->message = $message;
    }

    /**
     * @return null
     */
    public function getMessage()
    {
        return $this->message;
    }


} 
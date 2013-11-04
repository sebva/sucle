<?php

class Device
{
    private $id;
    private $device_id;
    private $type;
    private $os_version;

    public function __construct($data)
    {
        $this->id = isset($data['id']) ? $data['id'] : null;
        $this->device_id = isset($data['device_id']) ? $data['device_id'] : null;
        $this->type = isset($data['type']) ? $data['type'] : null;
        $this->os_version = isset($data['os_version']) ? $data['os_version'] : null;
    }

    public function getInfo()
    {
        return array('id' => $this->id,
                     'device_id' => $this->device_id,
                     'type' => $this->type,
                     'os_version' => $this->os_version);
    }

    /**
     * @return mixed
     */
    public function getDeviceId()
    {
        return $this->device_id;
    }

    /**
     * @return mixed
     */
    public function getType()
    {
        return $this->type;
    }

    /**
     * @return mixed
     */
    public function getOsVersion()
    {
        return $this->os_version;
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param null $device_id
     */
    public function setDeviceId($device_id)
    {
        $this->device_id = $device_id;
    }

    /**
     * @param null $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @param null $os_version
     */
    public function setOsVersion($os_version)
    {
        $this->os_version = $os_version;
    }

    /**
     * @param null $type
     */
    public function setType($type)
    {
        $this->type = $type;
    }


} 
<?php

class Token
{
    private $id;
    private $user;
    private $device;
    private $token;

    public function __construct($data)
    {
        $this->id = isset($data['id']) ? $data['id'] : null;
        $this->user = isset($data['user']) ? $data['user'] : null;
        $this->device = isset($data['device']) ? $data['device'] : null;
        $this->token = isset($data['token']) ? $data['token'] : null;
    }

    public function getInfo()
    {
        return array('id' => $this->id,
            'user' => $this->user->getInfo(),
            'device' => $this->device->getInfo(),
            'token' => $this->token);
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
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return mixed
     */
    public function getUser()
    {
        return $this->user;
    }

    /**
     * @param null $device
     */
    public function setDevice($device)
    {
        $this->device = $device;
    }

    /**
     * @param null $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @param null $social
     */
    public function setUser($user)
    {
        $this->user = $user;
    }

    /**
     * @param null $token
     */
    public function setToken($token)
    {
        $this->token = $token;
    }

    /**
     * @return null
     */
    public function getToken()
    {
        return $this->token;
    }


} 
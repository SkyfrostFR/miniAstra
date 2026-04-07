module.exports = {
  apps: [
    {
      name: 'miniastra-8080',
      cwd: './backend',
      script: 'start.sh',
      interpreter: 'bash',
      env: {
        SPRING_PROFILES_ACTIVE: 'dev'
      }
    },
    {
      name: 'miniastra-front',
      cwd: './frontend',
      script: 'npm',
      args: 'run dev',
      env: {
        NODE_ENV: 'development'
      }
    }
  ]
}

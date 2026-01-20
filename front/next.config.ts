import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  images: {
      remotePatterns: [
        {
          protocol: 'http',
          hostname: 'localhost',
          port: '9000',
          pathname: '/uxstudio-contacts/**',
        },
        {
          protocol: 'http',
          hostname: '127.0.0.1',
          port: '9000',
          pathname: '/uxstudio-contacts/**',
        },
        {
          protocol: 'https',
          hostname: 'minio.unilaunch.org',
          pathname: '/uxstudio-contacts/**',
        },
      ],
    },
};

export default nextConfig;

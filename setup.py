from distutils.core import setup

setup(
    name='Tsuro',
    version='0.1.0',
    author='Will Stogin, Eric Chang',
    author_email='wstogin@u.northwestern.edu, ericchang2017@u.northwestern.edu',
    packages=['Tsuro', 'Tsuro.test'],
    description='A game that allows users to play Tsuro.',
    long_description=open('README.txt').read(),
    install_requires=[
    ],
)\